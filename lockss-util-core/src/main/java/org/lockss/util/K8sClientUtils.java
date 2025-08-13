package org.lockss.util;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1NetworkPolicyList;
import io.kubernetes.client.util.ClientBuilder;

import java.io.IOException;
import java.util.Collections;

public class K8sClientUtils {
  private String includeIps;
  private String excludeIps;

  public static CoreV1Api getCoreV1Api() throws IOException {
    ApiClient client = ClientBuilder.cluster().build();
    Configuration.setDefaultApiClient(client);
    return new CoreV1Api(client);
  }

  public static ApiClient initClusterClient() throws IOException {
    ApiClient client = ClientBuilder.cluster().build();
    Configuration.setDefaultApiClient(client);
    return client;
  }

  public V1NetworkPolicyList getNetworkPolicies(String namespace) throws ApiException, IOException {
    // Get the API client
    ApiClient client = ClientBuilder.cluster().build();
    Configuration.setDefaultApiClient(client);

    // Create NetworkingV1Api instance
    NetworkingV1Api networkingV1Api = new NetworkingV1Api(client);

    try {
      // List network policies in the specified namespace
      V1NetworkPolicyList networkPolicyList = networkingV1Api.listNamespacedNetworkPolicy(
          namespace,     // namespace
          null,         // pretty
          null,         // allowWatchBookmarks
          null,         // _continue
          null,         // fieldSelector
          null,         // labelSelector
          null,         // limit
          null,         // resourceVersion
          null,         // resourceVersionMatch
          null,         // timeoutSeconds
          false        // watch
      );

      return networkPolicyList;
    } catch (ApiException e) {
      System.err.println("Exception when calling NetworkingV1Api#listNamespacedNetworkPolicy");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Response body: " + e.getResponseBody());
      throw e;
    }
  }

}