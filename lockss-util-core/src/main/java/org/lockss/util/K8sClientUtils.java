/*

Copyright (c) 2000-2025, Board of Trustees of Leland Stanford Jr. University

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

 */

package org.lockss.util;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;
import io.kubernetes.client.util.ClientBuilder;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class K8sClientUtils {

  private static  ApiClient defaultApiClient;
  private static  NetworkingV1Api cachedNetworkingApi;

  private K8sClientUtils() {
    // utility class
  }

  /**
   * Lazily obtains a memoized NetworkingV1Api instance using the default Kubernetes client.
   */
  private static NetworkingV1Api networkingApi() throws java.io.IOException {
    if (cachedNetworkingApi == null) {
      synchronized (K8sClientUtils.class) {
        if (cachedNetworkingApi == null) {
          ApiClient client = defaultApiClient;
          if (client == null) {
            client = ClientBuilder.cluster().build();
            io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
            defaultApiClient = client;
          }
          cachedNetworkingApi = new io.kubernetes.client.openapi.apis.NetworkingV1Api(client);
        }
      }
    }
    return cachedNetworkingApi;
  }


  /**
   * Reads a NetworkPolicy by name/namespace. Returns null if not found (404).
   */
  public static V1NetworkPolicy readNetworkPolicyOrNull(String name, String namespace)
      throws ApiException, IOException {
    try {
      return networkingApi().readNamespacedNetworkPolicy(name, namespace).execute();
    } catch (ApiException ae) {
      if (ae.getCode() == 404) {
        return null;
      }
      throw ae;
    }
  }

  /**
   * Creates a NetworkPolicy in the given namespace.
   */
  public static V1NetworkPolicy createNetworkPolicy(V1NetworkPolicy policy)
      throws ApiException, IOException {
    validatePolicyMeta(policy);
    ensureGvk(policy);
    return networkingApi()
        .createNamespacedNetworkPolicy(policy.getMetadata().getNamespace(), policy)
        .execute();
  }

  /**
   * Replaces an existing NetworkPolicy by name/namespace with the provided object.
   * Ensures resourceVersion is set to avoid 409 conflicts.
   */
  public static V1NetworkPolicy replaceNetworkPolicy(V1NetworkPolicy policy)
      throws ApiException, IOException {
    validatePolicyMeta(policy);
    ensureGvk(policy);

    String name = policy.getMetadata().getName();
    String ns = policy.getMetadata().getNamespace();

    return networkingApi()
        .replaceNamespacedNetworkPolicy(name, ns, policy)
        .execute();
  }

  public enum ApplyResult {
    CREATED,
    REPLACED,
    UNCHANGED
  }

  /**
   * Creates the NetworkPolicy if it does not exist, otherwise replaces it.
   * Returns CREATED or REPLACED accordingly.
   */
  public static ApplyResult createOrReplaceNetworkPolicy(V1NetworkPolicy policy)
      throws ApiException, IOException {
    validatePolicyMeta(policy);
    String name = policy.getMetadata().getName();
    String namespace = policy.getMetadata().getNamespace();

    try {
      // Read to determine existence; do not pass extra params
      V1NetworkPolicy existing =
          networkingApi().readNamespacedNetworkPolicy(name, namespace).execute();

      // Copy resourceVersion from existing before replace to avoid 409
      if (existing != null && existing.getMetadata() != null &&
          existing.getMetadata().getResourceVersion() != null) {
        policy.getMetadata().setResourceVersion(existing.getMetadata().getResourceVersion());
      }
      replaceNetworkPolicy(policy);
      return ApplyResult.REPLACED;
    } catch (ApiException ae) {
      if (ae.getCode() == 404) {
        createNetworkPolicy(policy);
        return ApplyResult.CREATED;
      }
      throw ae;
    }
  }


  public static void writeNetworkPolicyToFile(V1NetworkPolicy policy, String filename)
      throws IOException {
    validatePolicyMeta(policy);
    ensureGvk(policy);

    Path path = Paths.get(filename);
    Path parentDir = path.getParent();
    if (parentDir != null) {
      Files.createDirectories(parentDir);
    }

    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      // Get the ApiClient to use its serialization capabilities
      ApiClient apiClient = defaultApiClient;
      if (apiClient == null) {
        apiClient = io.kubernetes.client.util.Config.defaultClient();
      }

      // Use the Kubernetes client's YAML serializer
      String yamlContent = io.kubernetes.client.util.Yaml.dump(policy);

      // Write the content and ensure a trailing newline
      writer.write(yamlContent);
      if (!yamlContent.endsWith("\n")) {
        writer.write("\n");
      }
    } catch (Exception ex) {
      throw new IOException("Failed to write NetworkPolicy YAML to " + filename, ex);
    }
  }

  private static void validatePolicyMeta(V1NetworkPolicy policy) {
    if (policy == null || policy.getMetadata() == null) {
      throw new IllegalArgumentException(
          "NetworkPolicy and its metadata must be non-null");
    }
    String name = policy.getMetadata().getName();
    String ns = policy.getMetadata().getNamespace();
    if (name == null || name.isEmpty() || ns == null || ns.isEmpty()) {
      throw new IllegalArgumentException(
          "NetworkPolicy metadata (name, namespace) must be non-empty");
    }
  }

  // Ensure required apiVersion/kind for serialization and server validation
  private static void ensureGvk(V1NetworkPolicy policy) {
    if (policy.getApiVersion() == null || policy.getApiVersion().isEmpty()) {
      policy.setApiVersion("networking.k8s.io/v1");
    }
    if (policy.getKind() == null || policy.getKind().isEmpty()) {
      policy.setKind("NetworkPolicy");
    }
  }
}
