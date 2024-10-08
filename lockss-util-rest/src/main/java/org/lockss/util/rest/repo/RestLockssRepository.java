/*
 * Copyright (c) 2017-2019, Board of Trustees of Leland Stanford Jr. University,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.lockss.util.rest.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.lockss.log.L4JLogger;
import org.lockss.util.ListUtil;
import org.lockss.util.LockssUncheckedIOException;
import org.lockss.util.auth.AuthUtil;
import org.lockss.util.io.FileUtil;
import org.lockss.util.jms.JmsConsumer;
import org.lockss.util.jms.JmsFactory;
import org.lockss.util.jms.JmsProducer;
import org.lockss.util.jms.JmsUtil;
import org.lockss.util.rest.LockssResponseErrorHandler;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.multipart.MultipartMessage;
import org.lockss.util.rest.repo.model.*;
import org.lockss.util.rest.repo.util.*;
import org.lockss.util.storage.StorageInfo;
import org.lockss.util.time.Deadline;
import org.lockss.util.time.TimeUtil;
import org.lockss.util.time.TimerUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * REST client implementation of the LOCKSS Repository API; makes REST
 * calls to a remote LOCKSS Repository REST server.
 * <p>
 * Recently returned Artifacts are cached in an ArtifactCache so that
 * subsequent lookups can be satisfied without a REST roundtrip.
 */
public class RestLockssRepository implements LockssRepository {
  private final static L4JLogger log = L4JLogger.getLogger();
  private final static MultiValueMap<String, Object> EMPTY_MULTIPART_MAP = new LinkedMultiValueMap<>();

  public static final int DEFAULT_MAX_ART_CACHE_SIZE = 500;
  public static final int DEFAULT_MAX_ART_DATA_CACHE_SIZE = 20;

  public static final boolean DEFAULT_USE_MULTIPART_ENDPOINT = false;
  private boolean useMultipartEndpoint = DEFAULT_USE_MULTIPART_ENDPOINT;

  // These must match the LOCKSS Repository swagger specification:
  public static final String MULTIPART_ARTIFACT_PROPS = "artifactProps";
  public static final String MULTIPART_ARTIFACT_HTTP_RESPONSE_HEADER = "httpResponseHeader";
  public static final String MULTIPART_ARTIFACT_PAYLOAD = "payload";

  private RestTemplate restTemplate;
  private URL repositoryUrl;

  // The value of the Authorization header to be used when calling the REST
  // service.
  private String authHeaderValue = null;

  /**
   * Constructor that takes a base URL to a remote LOCKSS Repository service,
   * and uses an unmodified Spring REST template client.
   *
   * @param repositoryUrl A {@code URL} containing the base URL of the remote
   *                      LOCKSS Repository service.
   * @param userName      A String with the name of the user used to access the
   *                      remote LOCKSS Repository service.
   * @param password      A String with the password of the user used to access
   *                      the remote LOCKSS Repository service.
   */
  public RestLockssRepository(URL repositoryUrl, String userName, String password) throws IOException {
    this(repositoryUrl,
        RestUtil.getRestTemplate(0, 0, (int) (16 * FileUtils.ONE_MB), null),
        userName, password);
  }

  /**
   * Constructor that takes a base URL to a remote LOCKSS Repository service,
   * and an instance of Spring's {@code RestTemplate}. Used for mainly for
   * testing.
   *
   * @param repositoryUrl A {@code URL} containing the base URL of the remote
   *                      LOCKSS Repository service.
   * @param restTemplate  Instance of {@code RestTemplate} to use internally for
   *                      remote REST calls.
   * @param userName      A String with the name of the user used to access the
   *                      remote LOCKSS Repository service.
   * @param password      A String with the password of the user used to access
   *                      the remote LOCKSS Repository service.
   */
  public RestLockssRepository(URL repositoryUrl, RestTemplate restTemplate, String userName, String password)
      throws IOException {

    // Set RestTemplate used by RestLockssRepository
    this.restTemplate = restTemplate;

    // Set remote Repository service URL
    this.repositoryUrl = repositoryUrl;

    // Check whether user credentials were passed.
    if (userName != null && password != null) {
      authHeaderValue = AuthUtil.basicAuthHeaderValue(userName, password);
    }

    log.trace("authHeaderValue = {}", authHeaderValue);

    // Install our custom ResponseErrorHandler in the RestTemplate used by this RestLockssRepository
    restTemplate.setErrorHandler(new LockssResponseErrorHandler(restTemplate.getMessageConverters()));

    File tmpDir = FileUtil.createTempDir("repo-client", null);

    // Add the multipart/form-data converter to the RestTemplate
    RestUtil.addMultipartConverter(restTemplate, tmpDir);
  }

  /**
   * Constructs a REST endpoint to an artifact in the repository.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param artifactUuid A {@code String} containing the artifact UUID.
   * @return A {@code URI} containing the REST endpoint to an artifact in the repository.
   */
  private URI artifactEndpoint(String namespace, String artifactUuid) {
    return artifactEndpoint(namespace, artifactUuid, null);
  }

  /**
   * Constructs a REST endpoint to an artifact in the repository.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param artifactUuid A {@code String} containing the artifact UUID.
   * @param includeContent An {@link IncludeContent} indicating whether the artifact content part can or should be
   *                       included in the multipart response. Default is {@link IncludeContent#ALWAYS}.
   * @return A {@code URI} containing the REST endpoint to an artifact in the repository.
   */
  private URI artifactEndpoint(String namespace, String artifactUuid, IncludeContent includeContent) {
    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("uuid", artifactUuid);

    Map<String, String> queryParams = new HashMap<>();

    if (namespace != null) {
      queryParams.put("namespace", namespace);
    }

    if (includeContent != null) {
      // includeContent defaults to ALWAYS but lets be explicit to avoid confusion
      queryParams.put("includeContent", includeContent.toString());
    }

    return RestUtil.getRestUri(repositoryUrl + "/artifacts/{uuid}", uriParams, queryParams);
  }

  private enum ArtifactDataType {
    response("response"),
    resource("payload");

    private String endpoint;

    ArtifactDataType(String endpoint) {
      this.endpoint = endpoint;
    }

    public String getEndpoint() {
      return endpoint;
    }
  }

  private URI artifactDataEndpoint(String namespace, String artifactUuid, ArtifactDataType type,
                                   IncludeContent includeContent) {

    Map<String, String> uriParams = new HashMap<>();
    Map<String, String> queryParams = new HashMap<>();

    // URI path parameters
    uriParams.put("uuid", artifactUuid);
    uriParams.put("endpoint", type.getEndpoint());

    // Query parameters
    if (namespace != null) {
      queryParams.put("namespace", namespace);
    }

    if (includeContent != null) {
      queryParams.put("includeContent", includeContent.toString());
    }

    return RestUtil.getRestUri(repositoryUrl + "/artifacts/{uuid}/{endpoint}", uriParams, queryParams);
  }

  /**
   * Throws LockssNoSuchArtifactIdException if the response status is 404,
   * otherwise returns
   *
   * @param e          the LockssRestHttpException that was caught
   * @param artifactUuid A {@code String} containing the artifact UUID.
   * @param msg        used in error log and thrown exception
   */
  private void checkArtIdError(LockssRestHttpException e, String artifactUuid,
                               String msg)
      throws LockssNoSuchArtifactIdException {
    if (e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
      throw new LockssNoSuchArtifactIdException(msg + ": " + artifactUuid, e);
    }
  }

  /**
   * Adds an instance of {@code ArtifactData} to the remote REST LOCKSS Repository server.
   * <p>
   * Encodes an {@code ArtifactData} and its constituent parts into a multipart/form-data HTTP POST request for
   * transmission to a remote LOCKSS repository.
   *
   * @param artifactData An {@code ArtifactData} to add to the remote LOCKSS repository.
   * @return A {@code String} containing the artifact ID of the newly added artifact.
   */
  @Override
  public Artifact addArtifact(ArtifactData artifactData) throws IOException {
    if (artifactData == null) {
      throw new IllegalArgumentException("Null ArtifactData");
    }

    // Get artifact identifier
    ArtifactIdentifier artifactId = artifactData.getIdentifier();

    log.debug("Adding artifact to remote repository [namespace: {}, auId: {}, uri: {}]",
        artifactId.getNamespace(), artifactId.getAuid(), artifactId.getUri());

    // Transform ArtifactData into multiparts
    MultiValueMap<String, Object> parts =
        ArtifactDataUtil.generateMultipartMapFromArtifactData(artifactData, IncludeContent.ALWAYS, 0);

    // POST request body
    HttpEntity<MultiValueMap<String, Object>> multipartEntity =
        new HttpEntity<>(parts, getInitializedHttpHeaders());

    // Build REST endpoint to /artifacts
    String endpoint = String.format("%s/artifacts", repositoryUrl);
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);

    // Perform REST call: POST the multipart entity to the remote LOCKSS repository
    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.POST,
              multipartEntity,
              // TODO: Change this to Artifact and remove OjectMapper below
              String.class, "addArtifact");

      // Handle response
      checkStatusOk(response);

      // TODO
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      Artifact res = mapper.readValue(response.getBody(), Artifact.class);

      artCache.put(res);
      artCache.putArtifactData(res.getNamespace(), res.getIdentifier().getUuid(), artifactData);

      return res;
    } catch (LockssRestException e) {
      log.error("Could not add artifact", e);
      throw e;
    }
  }

  private static final MediaType APPLICATION_WARC = MediaType.valueOf("application/warc");
  private static final String DOT_COMPRESSED_WARC_FILE_EXTENSION = ".warc.gz";
  private static final String DOT_WARC_FILE_EXTENSION = ".warc";

  /**
   * Adds artifacts from an archive to the remote LOCKSS Repository Service.
   *
   * @param namespace A {@link String} containing the namespace of the artifacts.
   * @param auId         A {@link String} containing the AUID of the artifacts.
   * @param inputStream  The {@link InputStream} of the archive.
   * @param type         A {@link ArchiveType} indicating the type of archive.
   * @param storeDuplicate A {@code boolean} indicating whether new versions of artifacets whose content would be identical to the previous version should be stored
   * @param excludeStatusPattern    A {@link String} containing a regexp.  WARC records whose HTTP response status code matches will not be added to the repository
   * @return
   */
  @Override
  public ImportStatusIterable addArtifacts(String namespace, String auId, InputStream inputStream,
                                           ArchiveType type, boolean storeDuplicate, String excludeStatusPattern) throws IOException {

    if (type != ArchiveType.WARC) {
      throw new NotImplementedException("Archive not supported");
    }

    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

    // Attach AUID part
    parts.add("auid", auId);

    // Attach archive part
    HttpHeaders archiveHeaders = new HttpHeaders();

    // FIXME: Content-Length must be set to avoid the InputStream from being read to
    //  determine the Content-Length
    archiveHeaders.setContentLength(0);
    archiveHeaders.setContentType(APPLICATION_WARC);

    Resource archiveResource = new NamedInputStreamResource("archive", inputStream);

    parts.add("archive", new HttpEntity<>(archiveResource, archiveHeaders));

    // Prepare the endpoint URI
    String archivesEndpoint = repositoryUrl + "/archives";

    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("namespace", namespace);
    if (storeDuplicate) {
      queryParams.put("storeDuplicate", "true");
    }
    if (!StringUtils.isEmpty(excludeStatusPattern)) {
      queryParams.put("excludeStatusPattern", excludeStatusPattern);
    }

    try {
      ResponseEntity<Resource> response =
          RestUtil.callRestService(restTemplate,
              RestUtil.getRestUri(archivesEndpoint, null, queryParams),
              HttpMethod.POST,
              new HttpEntity<>(parts, getInitializedHttpHeaders()),
              Resource.class, "Error calling remote addArtifacts() over REST");

      checkStatusOk(response);

      Resource resource = response.getBody();

      return new ImportStatusIterable(resource.getInputStream());
    } catch (LockssRestException e) {
      log.error("Could not add archive", e);
      throw e;
    }
  }

  /**
   * Returns the {@link Artifact} with the specified UUID
   *
   * @param artifactUuid
   * @return The {@code Artifact} with the UUID, or null if none
   * @throws IOException
   */
  public Artifact getArtifactFromUuid(String artifactUuid) throws IOException {
    throw new UnsupportedOperationException();
  }

  MediaType APPLICATION_HTTP_RESPONSE = MediaType.parseMediaType("application/http;msgtype=response");


  /**
   * Retrieves an artifact from a remote REST LOCKSS Repository server.
   *
   * @param artifact The {@link Artifact} of the {@link ArtifactData} to return.
   * @param includeContent A {@link IncludeContent} indicating whether the artifact content should be included in the
   *                       {@link ArtifactData} returned by this method.
   * @return The {@link ArtifactData} of the {@link Artifact}.
   * @throws IOException
   */
  @Override
  public ArtifactData getArtifactData(Artifact artifact, IncludeContent includeContent)
      throws IOException {

    if (artifact == null) {
      throw new IllegalArgumentException("Null artifact");
    }

    String namespace = artifact.getNamespace();
    String artifactUuid = artifact.getUuid();

    // Check ArtifactCache first
    boolean needInputStream = (includeContent != IncludeContent.NEVER);
    ArtifactData cached = artCache.getArtifactData(namespace, artifactUuid, needInputStream);
    if (cached != null) {
      return cached;
    }

    if (useMultipartEndpoint) {
      return getArtifactDataByMultipart(namespace, artifactUuid, includeContent);
    }

    try {
      URI endpoint = artifactDataEndpoint(namespace, artifactUuid, ArtifactDataType.response, includeContent);

      // Set Accept header in request
      HttpHeaders requestHeaders = getInitializedHttpHeaders();
      requestHeaders.setAccept(ListUtil.list(APPLICATION_HTTP_RESPONSE, MediaType.APPLICATION_JSON));

      // Make the request to the REST service and get its response
      ResponseEntity<InputStreamResource> response = RestUtil.callRestService(
          restTemplate,
          endpoint,
          HttpMethod.GET,
          new HttpEntity<>(requestHeaders),
          InputStreamResource.class,
          "REST client error: getArtifactData()");

      checkStatusOk(response);

      // Transform HTTP response stream in REST response body to ArtifactData
      Resource body = response.getBody();
      HttpHeaders responseHeaders = response.getHeaders();

      boolean receivedOnlyHeaders =
          responseHeaders.containsKey(ArtifactConstants.INCLUDES_CONTENT) &&
          responseHeaders.getFirst(ArtifactConstants.INCLUDES_CONTENT).equals("false");

      boolean receivedResourceType =
          responseHeaders.containsKey(ArtifactConstants.ARTIFACT_DATA_TYPE) &&
          responseHeaders.getFirst(ArtifactConstants.ARTIFACT_DATA_TYPE).equals("resource");

      InputStream responseBodyStream = body.getInputStream();

      ArtifactData result;

      try {
        if (receivedOnlyHeaders) {
          HttpResponse httpResponse = ArtifactDataUtil.getHttpResponseFromStream(responseBodyStream);

          result = new ArtifactData()
              .setHttpStatus(httpResponse.getStatusLine())
              .setHttpHeaders(ArtifactDataUtil.transformHeaderArrayToHttpHeaders(httpResponse.getAllHeaders()));

          if (receivedResourceType)
            result.setHttpStatus(null);

        } else if (receivedResourceType) {
          HttpResponse httpResponse = ArtifactDataUtil.getHttpResponseFromStream(responseBodyStream);

          result = new ArtifactData()
              .setHttpHeaders(ArtifactDataUtil.transformHeaderArrayToHttpHeaders(httpResponse.getAllHeaders()))
              .setInputStream(httpResponse.getEntity().getContent());
        } else {
          result = new ArtifactData()
              .setResponseInputStream(responseBodyStream);
        }
      } catch (HttpException e) {
        throw new LockssRestHttpException("Malformed HTTP response in REST response body");
      }

      // Populate ArtifactData properties from Artifact
      result
          .setIdentifier(artifact.getIdentifier())
          .setContentLength(artifact.getContentLength())
          .setContentDigest(artifact.getContentDigest());

      // FIXME: Instances of Artifact from RestLockssRepository don't have a meaningful storage URL
      //        at this time and probably never will but if it has one, set it on the ArtifactData.
      if (artifact.getStorageUrl() != null) {
          result.setStorageUrl(URI.create(artifact.getStorageUrl()));
      }

      String storeDate = responseHeaders.getFirst(ArtifactConstants.ARTIFACT_STORE_DATE_KEY);
      if (!(storeDate == null || storeDate.isEmpty())) {
        TemporalAccessor t = DateTimeFormatter.ISO_INSTANT.parse(storeDate);
        result.setStoreDate(ZonedDateTime.ofInstant(Instant.from(t), ZoneOffset.UTC).toInstant().toEpochMilli());
      }

      // Add to artifact data cache
      artCache.putArtifactData(namespace, artifactUuid, result);

      return result;
    } catch (LockssRestHttpException e) {
      checkArtIdError(e, artifactUuid, "Artifact not found");
      log.error("Could not get artifact data", e);
      throw e;
    } catch (LockssRestException e) {
      log.error("Could not get artifact data", e);
      throw e;
    }
  }

  public ArtifactData getArtifactDataByPayload(Artifact artifact, IncludeContent includeContent)
      throws IOException {

    if (artifact == null) {
      throw new IllegalArgumentException("Null artifact");
    }

    String namespace = artifact.getNamespace();
    String artifactUuid = artifact.getUuid();

    // Check ArtifactCache first
    boolean needInputStream = (includeContent != IncludeContent.NEVER);
    ArtifactData cached = artCache.getArtifactData(namespace, artifactUuid, needInputStream);
    if (cached != null) {
      return cached;
    }

    try {
      URI endpoint = artifactDataEndpoint(namespace, artifactUuid, ArtifactDataType.resource, includeContent);

      // Set Accept header in request
      HttpHeaders requestHeaders = getInitializedHttpHeaders();
      requestHeaders.setAccept(ListUtil.list(MediaType.ALL, MediaType.APPLICATION_JSON));

      // Make the request to the REST service and get its response
      ResponseEntity<Resource> response = RestUtil.callRestService(
          restTemplate,
          endpoint,
          HttpMethod.GET,
          new HttpEntity<>(requestHeaders),
          Resource.class,
          "REST client error: getArtifactDataByPayload()");

      checkStatusOk(response);

      // Transform HTTP response stream in REST response body to ArtifactData
      Resource body = response.getBody();
      HttpHeaders responseHeaders = response.getHeaders();

      boolean receivedOnlyHeaders =
          responseHeaders.containsKey(ArtifactConstants.INCLUDES_CONTENT) &&
              responseHeaders.getFirst(ArtifactConstants.INCLUDES_CONTENT).equals("false");

      HttpHeaders artifactHeaders = new HttpHeaders();

      if (responseHeaders.getContentType() != null) {
        artifactHeaders.setContentType(responseHeaders.getContentType());
      }

      artifactHeaders.setContentLength(responseHeaders.getContentLength());
      artifactHeaders.set(ArtifactConstants.ARTIFACT_DIGEST_KEY,
          responseHeaders.getFirst(ArtifactConstants.ARTIFACT_DIGEST_KEY));

      ArtifactData result = new ArtifactData()
          .setHttpHeaders(artifactHeaders);

      if (!receivedOnlyHeaders) {
        InputStream responseBodyStream = body.getInputStream();
        result.setInputStream(responseBodyStream);
      }

      // Populate ArtifactData properties from Artifact
      result
          .setIdentifier(artifact.getIdentifier())
          .setContentLength(artifact.getContentLength())
          .setContentDigest(artifact.getContentDigest());

      // FIXME: Instances of Artifact from RestLockssRepository don't have a meaningful storage URL
      //        at this time and probably never will but if it has one, set it on the ArtifactData.
      if (artifact.getStorageUrl() != null) {
        result.setStorageUrl(URI.create(artifact.getStorageUrl()));
      }

      String storeDate = responseHeaders.getFirst(ArtifactConstants.ARTIFACT_STORE_DATE_KEY);
      if (!(storeDate == null || storeDate.isEmpty())) {
        TemporalAccessor t = DateTimeFormatter.ISO_INSTANT.parse(storeDate);
        result.setStoreDate(ZonedDateTime.ofInstant(Instant.from(t), ZoneOffset.UTC).toInstant().toEpochMilli());
      }

      // Add to artifact data cache
      artCache.putArtifactData(namespace, artifactUuid, result);

      return result;
    } catch (LockssRestHttpException e) {
      checkArtIdError(e, artifactUuid, "Artifact not found");
      log.error("Could not get artifact data", e);
      throw e;
    } catch (LockssRestException e) {
      log.error("Could not get artifact data", e);
      throw e;
    }
  }

  public ArtifactData getArtifactDataByMultipart(String namespace, String artifactUuid, IncludeContent includeContent)
    throws IOException {
    if (artifactUuid == null) {
      throw new IllegalArgumentException("Null artifact UUID");
    }

    // Cache policy: Include cache content unless IncludeContent.NEVER
    boolean includeCachedContent = (includeContent != IncludeContent.NEVER);

    // Get ArtifactData from cache
    ArtifactData cached = artCache.getArtifactData(namespace, artifactUuid, includeCachedContent);

    if (cached != null) {
      // Cache hit: Return cached ArtifactData
      return cached;
    }

    // Cache miss: Fetch ArtifactData from repository service
    try {
      URI artifactEndpoint = artifactEndpoint(namespace, artifactUuid, includeContent);

      // Set Accept header in request
      HttpHeaders requestHeaders = getInitializedHttpHeaders();
      requestHeaders.setAccept(
          // Order matters! We expect a multipart response if success or JSON error message otherwise
          ListUtil.list(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON));

      // Make the request to the REST service and get its response
      ResponseEntity<MultipartMessage> response = RestUtil.callRestService(
          restTemplate,
          artifactEndpoint,
          HttpMethod.GET,
          new HttpEntity<>(requestHeaders),
          MultipartMessage.class,
          "REST client error: getArtifactDataByMultipart()");

      checkStatusOk(response);

      // Transform MultipartMessage to ArtifactData
      ArtifactData result = ArtifactDataUtil.fromTransportResponseEntity(response);

      // Add to artifact data cache
      artCache.putArtifactData(namespace, artifactUuid, result);

      return result;

    } catch (LockssRestHttpException e) {
      checkArtIdError(e, artifactUuid, "Artifact not found");
      log.error("Could not get artifact data", e);
      throw e;

    } catch (LockssRestException e) {
      log.error("Could not get artifact data", e);
      throw e;
    }
  }

  /**
   * Commits an artifact to this LOCKSS repository for permanent storage and inclusion in LOCKSS repository queries.
   *
   * @param namespace A {code String} containing the namespace ID containing the artifact to commit.
   * @param artifactUuid A {@code String} with the UUID of the artifact to commit to the repository.
   * @return An {@code Artifact} containing the updated artifact state information.
   * @throws IOException
   */
  @Override
  public Artifact commitArtifact(String namespace, String artifactUuid) throws IOException {
    if (StringUtils.isEmpty(artifactUuid)) {
      throw new IllegalArgumentException("Null artifact UUID");
    }

    UriComponentsBuilder builder = UriComponentsBuilder.fromUri(artifactEndpoint(namespace, artifactUuid))
        .queryParam("committed", "true");

    // Required by REST API specification
    HttpHeaders headers = getInitializedHttpHeaders();
    headers.setContentType(MediaType.valueOf("multipart/form-data"));

    // Need to send an empty multipart request; empty body will cause undefined
    // behavior. In particular, Spring Boot 3.x will complain about a missing
    // multipart boundary.
    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.PUT,
              new HttpEntity<>(EMPTY_MULTIPART_MAP, headers),
              String.class,
              "commitArtifact client error");
      checkStatusOk(response);

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
          false);
      Artifact res = mapper.readValue(response.getBody(), Artifact.class);
      // Possible to commit out-of-order so we don't know whether this is
      // the latest
      artCache.put(res);
      return res;
    } catch (LockssRestHttpException e) {
      checkArtIdError(e, artifactUuid, "Could not commit; non-existent artifact");
      log.error("Could not commit artifact [uuid: {}]", artifactUuid, e);
      throw e;
    } catch (LockssRestException e) {
      log.error("Could not commit artifact [uuid: {}]", artifactUuid, e);
      throw e;
    }
  }

  /**
   * Permanently removes an artifact from this LOCKSS repository.
   *
   * @param artifact The artifact to remove from this LOCKSS repository.
   * @throws IOException
   */
  public void deleteArtifact(Artifact artifact) throws IOException {
    artCache.invalidateArtifact(ArtifactCache.InvalidateOp.Delete,
        artifact.makeKey());
    deleteArtifact(artifact.getNamespace(), artifact.getUuid());
  }

  /**
   * Permanently removes an artifact from this LOCKSS repository.
   *
   * @param namespace A {code String} containing the namespace ID of the namespace containing the artifact to delete.
   * @param artifactUuid A {@code String} with the artifact ID of the artifact to remove from this LOCKSS repository.
   * @throws IOException
   */
  @Override
  public void deleteArtifact(String namespace, String artifactUuid) throws IOException {
    if (StringUtils.isEmpty(artifactUuid)) {
      throw new IllegalArgumentException("Null artifact UUID");
    }

    HttpHeaders headers = getInitializedHttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      ResponseEntity<Void> response =
          RestUtil.callRestService(restTemplate,
              artifactEndpoint(namespace, artifactUuid),
              HttpMethod.DELETE,
              new HttpEntity<>(null, headers),
              Void.class, "deleteArtifact");

      checkStatusOk(response);
      HttpStatusCode statusCode = response.getStatusCode();

      if (statusCode.is2xxSuccessful()) {
        return;
      }

    } catch (LockssRestHttpException e) {
      checkArtIdError(e, artifactUuid, "Could not remove artifact id");
      log.error("Could not remove artifact [uuid: {}]", artifactUuid, e);
      throw e;
    } catch (LockssRestException e) {
      log.error("Could not remove artifact [uuid: {}]", artifactUuid, e);
      throw e;
    }
  }

//  /**
//   * Returns a boolean indicating whether an artifact is committed in this LOCKSS repository.
//   *
//   * @param artifactUuid UUID of the artifact to check committed status.
//   * @return A boolean indicating whether the artifact is committed.
//   */
//  @Override
//  public Boolean isArtifactCommitted(String namespace, String artifactUuid)
//      throws IOException {
//
//    if (StringUtils.isEmpty(artifactUuid)) {
//      throw new IllegalArgumentException("Null artifact UUID");
//    }
//
////    // Retrieve artifact from the artifact cache if cached
////    ArtifactData cached = artCache.getArtifactData(namespace, artifactUuid, false);
////    Artifact cachedArt = artCache.get(namespace, artifactUuid, false);
////
////    if (cached == null) {
////      ArtifactData ad = getArtifactData(namespace, artifactUuid, IncludeContent.IF_SMALL);
////      // TODO: IOUtils.closeQuietly(ad.getInputStream());
////    }
//
//    return getArtifactFromUuid(artifactUuid).isCommitted();
//  }

  /**
   * Provides the namespace of the committed artifacts in the index.
   *
   * @return An {@code Iterator<String>} with the namespaces of committed artifacts.
   */
  @Override
  public Iterable<String> getNamespaces() throws IOException {
    String endpoint = String.format("%s/namespaces", repositoryUrl);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "getNamespaces");
      checkStatusOk(response);

      ObjectMapper mapper = new ObjectMapper();
      List<String> result = mapper.readValue((String) response.getBody(),
          new TypeReference<List<String>>() {
          });
      return IteratorUtils.asIterable(result.iterator());
    } catch (LockssRestException e) {
      log.error("Could not get namespaces", e);
      throw e;
    }
  }

  /**
   * Returns an iterable over Archival Unit IDs (AUIDs) in this LOCKSS repository.
   *
   * @param namespace A {@code String} containing the namespace.
   * @return A {@code Iterable<String>} iterating over the AUIDs in this namespace.
   */
  @Override
  public Iterable<String> getAuIds(String namespace) throws IOException {
    String endpoint = String.format("%s/aus", repositoryUrl);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    try {
      return IteratorUtils.asIterable(
          new RestLockssRepositoryAuidIterator(restTemplate, builder, authHeaderValue));

    } catch (LockssUncheckedIOException e) {
      // Re-throw wrapped checked IOException
      throw e.getIOCause();
    }
  }

  /**
   * Returns an iterator over artifacts, given a REST endpoint that returns artifacts.
   *
   * @param builder A {@code UriComponentsBuilder} containing a REST endpoint that returns artifacts.
   * @return An {@code Iterator<Artifact>} containing artifacts.
   */
  private Iterator<Artifact> getArtifactIterator(UriComponentsBuilder builder) throws IOException {
    return new RestLockssRepositoryArtifactIterator(restTemplate, builder,
        authHeaderValue);
  }

  /**
   * Returns an iterable object over artifacts, given a REST endpoint that returns artifacts.
   *
   * @param endpoint A {@code URI} containing a REST endpoint that returns artifacts.
   * @return An {@code Iterator<Artifact>} containing artifacts.
   */
  private Iterator<Artifact> getArtifacts(URI endpoint) throws IOException {
    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              endpoint,
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "getArtifacts");
      checkStatusOk(response);

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
          false);
      List<Artifact> result = mapper.readValue((String) response.getBody(),
          ArtifactPageInfo.class).getArtifacts();
      return result.iterator();

    } catch (LockssRestHttpException e) {
      if (e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
        return Collections.emptyIterator();
      }
      log.error("Could not fetch artifacts", e);
      throw e;
    } catch (LockssRestException e) {
      log.error("Could not fetch artifacts", e);
      throw e;
    }

  }

  /**
   * Returns the committed artifacts of the latest version of all URLs, from a specified Archival Unit and namespace.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param auid       A {@code String} containing the Archival Unit ID.
   * @return An {@code Iterator<Artifact>} containing the latest version of all URLs in an AU.
   * @throws IOException
   */
  @Override
  public Iterable<Artifact> getArtifacts(String namespace, String auid) throws IOException {
    if (auid == null) {
      throw new IllegalArgumentException("Null AUID");
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("version", "latest");

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(artCache.cachingLatestIterator(getArtifactIterator(builder)));
  }

  /**
   * Returns the committed artifacts of all versions of all URLs, from a specified Archival Unit and namespace.
   *
   * @param namespace A String with the namespace.
   * @param auid       A String with the Archival Unit identifier.
   * @return An {@code Iterator<Artifact>} containing the committed artifacts of all version of all URLs in an AU.
   */
  @Override
  public Iterable<Artifact> getArtifactsAllVersions(String namespace, String auid) throws IOException {
    if (auid == null) {
      throw new IllegalArgumentException("Null AUID");
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("version", "all");

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(getArtifactIterator(builder));
  }

  /**
   * Returns the committed artifacts of the latest version of all URLs matching a prefix, from a specified Archival
   * Unit and namespace.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param auid       A {@code String} containing the Archival Unit ID.
   * @param prefix     A {@code String} containing a URL prefix.
   * @return An {@code Iterator<Artifact>} containing the latest version of all URLs matching a prefix in an AU.
   * @throws IOException
   */
  @Override
  public Iterable<Artifact> getArtifactsWithPrefix(String namespace, String auid, String prefix) throws IOException {
    if (auid == null || prefix == null) {
      throw new IllegalArgumentException("Null AUID or URL prefix");
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("urlPrefix", prefix);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(artCache.cachingLatestIterator(getArtifactIterator(builder)));
  }

  /**
   * Returns the committed artifacts of all versions of all URLs matching a prefix, from a specified Archival Unit and
   * namespace.
   *
   * @param namespace A String with the namespace.
   * @param auid       A String with the Archival Unit identifier.
   * @param prefix     A String with the URL prefix.
   * @return An {@code Iterator<Artifact>} containing the committed artifacts of all versions of all URLs matching a
   * prefix from an AU.
   */
  @Override
  public Iterable<Artifact> getArtifactsWithPrefixAllVersions(String namespace, String auid, String prefix) throws IOException {
    if (auid == null || prefix == null) {
      throw new IllegalArgumentException("Null AUID or URL prefix");
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("version", "all")
        .queryParam("urlPrefix", prefix);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(getArtifactIterator(builder));
  }

  /**
   * Returns the committed artifacts of all versions of all URLs matching a prefix, from a namespace.
   *
   * @param namespace A String with the namespace.
   * @param prefix     A String with the URL prefix.
   * @return An {@code Iterator<Artifact>} containing the committed artifacts of all versions of all URLs matching a
   * prefix.
   */
  @Override
  public Iterable<Artifact> getArtifactsWithUrlPrefixFromAllAus(String namespace, String prefix,
                                                                ArtifactVersions versions) throws IOException {

    if (prefix == null) {
      throw new IllegalArgumentException("Null URL prefix");
    }

    String endpoint = String.format("%s/artifacts", repositoryUrl);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("urlPrefix", prefix)
        .queryParam("versions", versions);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(artCache.cachingLatestIterator(getArtifactIterator(builder)));
  }

  /**
   * Returns the committed artifacts of all versions of a given URL, from a specified Archival Unit and namespace.
   *
   * @param namespace A {@code String} with the namespace.
   * @param auid       A {@code String} with the Archival Unit identifier.
   * @param url        A {@code String} with the URL to be matched.
   * @return An {@code Iterator<Artifact>} containing the committed artifacts of all versions of a given URL from an
   * Archival Unit.
   */
  @Override
  public Iterable<Artifact> getArtifactsAllVersions(String namespace, String auid, String url) throws IOException {
    if (auid == null || url == null) {
      throw new IllegalArgumentException("Null AUID or URL");
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("url", url)
        .queryParam("version", "all");

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(getArtifactIterator(builder));
  }

  /**
   * Returns the committed artifacts of all versions of a given URL, from a specified namespace.
   *
   * @param namespace A {@code String} with the namespace.
   * @param url        A {@code String} with the URL to be matched.
   * @return An {@code Iterator<Artifact>} containing the committed artifacts of all versions of a given URL.
   */
  @Override
  public Iterable<Artifact> getArtifactsWithUrlFromAllAus(String namespace, String url, ArtifactVersions versions) throws IOException {
    if (url == null) {
      throw new IllegalArgumentException("Null URL");
    }

    String endpoint = String.format("%s/artifacts", repositoryUrl);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("url", url)
        .queryParam("versions", versions);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    return IteratorUtils.asIterable(artCache.cachingLatestIterator(getArtifactIterator(builder)));
  }

  /**
   * Returns the artifact of the latest version of given URL, from a specified Archival Unit and namespace.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param auid       A {@code String} containing the Archival Unit ID.
   * @param url        A {@code String} containing a URL.
   * @return The {@code Artifact} representing the latest version of the URL in the AU.
   * @throws IOException
   */
  @Override
  public Artifact getArtifact(String namespace, String auid, String url) throws IOException {
    if (auid == null || url == null) {
      throw new IllegalArgumentException("Null AUID or URL");
    }

    Artifact cached = artCache.getLatest(namespace, auid, url);
    if (cached != null) {
      return cached;
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("url", url)
        .queryParam("version", "latest");

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "getArtifact");

      checkStatusOk(response);

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
          false);
      List<Artifact> artifacts = mapper.readValue(response.getBody(),
          ArtifactPageInfo.class).getArtifacts();

      if (!artifacts.isEmpty()) {
        if (artifacts.size() > 1) {
          log.warn(String.format(
              "Expected one or no artifacts for latest version but got %d (Namespace: %s, AU: %s, URL: %s)",
              artifacts.size(),
              namespace,
              url,
              auid
          ));
        }

        Artifact res = artifacts.get(0);
        if (res != null) {
          // This is the latest, cache as that as well as real version
          artCache.putLatest(res);
        }
        return res;
      }
      // No artifact found
      return null;

    } catch (LockssRestHttpException e) {
      if (!e.getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
        log.error("Could not fetch artifact", e);
      }
      return null;
    } catch (LockssRestException e) {
      log.error("Could not fetch artifact", e);
      throw e;
    }
  }

  /**
   * Returns the artifact of a given version of a URL, from a specified Archival Unit and namespace.
   *
   * @param namespace         A String with the namespace.
   * @param auid               A String with the Archival Unit identifier.
   * @param url                A String with the URL to be matched.
   * @param version            An Integer with the version.
   * @param includeUncommitted A boolean with the indication of whether an uncommitted artifact
   *                           may be returned.
   * @return The {@code Artifact} of a given version of a URL, from a specified AU and namespace.
   */
  @Override
  public Artifact getArtifactVersion(String namespace, String auid, String url, Integer version,
                                     boolean includeUncommitted) throws IOException {

    if (auid == null || url == null || version == null) {
      throw new IllegalArgumentException("Null AUID, URL or version");
    }

    Artifact cached = artCache.get(namespace, auid, url, version);
    if (cached != null) {
      return cached;
    }

    String endpoint = String.format("%s/aus/%s/artifacts", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("url", url)
        .queryParam("version", version);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    if (includeUncommitted) {
      builder.queryParam("includeUncommitted", includeUncommitted);
    }

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "getArtifactVersion");
      checkStatusOk(response);

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
          false);
      List<Artifact> artifacts = mapper.readValue((String) response.getBody(),
          ArtifactPageInfo.class).getArtifacts();

      if (!artifacts.isEmpty()) {
        // Warn if the server returned more than one artifact
        if (artifacts.size() > 1) {
          log.warn(String.format("Expected one or no artifacts but got %d (Namespace: %s, AU: %s, URL: %s, Version: " +
                  "%s)",
              artifacts.size(),
              namespace,
              auid,
              url,
              version
          ));
        }

        Artifact res = artifacts.get(0);
        artCache.put(res);
        return res;
      }

      // No artifact found
      return null;

    } catch (LockssRestException e) {
      log.error("Could not fetch versioned artifact", e);
      return null;
    }
  }

  /** Start a bulk store operation for the namespace/auid.
   * Substantially speeds Artifact creation, but the index isn't
   * permanently updated until a matching {@link
   * #finishBulkStore(String, String)} completes) */
  public void startBulkStore(String namespace, String auid)
      throws IOException {
    doBulkOp(namespace, auid, "start");
  }

  /** Finish a bulk store operation for the namespace/auid.  Blocks
   * until the Artifacts have been moved to the permanent ArtifactIndex. */
  public void finishBulkStore(String namespace, String auid)
      throws IOException {
    doBulkOp(namespace, auid, "finish");
  }

  void doBulkOp(String namespace, String auid, String op) throws IOException {
    if (auid == null) {
      throw new IllegalArgumentException("Null AUID");
    }

    String endpoint = String.format("%s/aus/%s/bulk", repositoryUrl, auid);
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("op", op);

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    // Required by REST API specification
    HttpHeaders headers = getInitializedHttpHeaders();
    headers.setContentType(MediaType.valueOf("multipart/form-data"));

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.PUT,
              new HttpEntity<>(null, headers),
              String.class,
              "startBulk");
      checkStatusOk(response);
    } catch (LockssRestException e) {
      log.error("Could not start bulk for: {}", auid, e);
      throw e;
    }
  }

  /**
   * Returns the size, in bytes, of AU in a namespace.
   *
   * @param namespace A {@code String} containing the namespace.
   * @param auid       A {@code String} containing the Archival Unit ID.
   * @return A {@link AuSize} with byte size statistics of the specified AU.
   */
  @Override
  public AuSize auSize(String namespace, String auid) throws IOException {
    if (auid == null) {
      throw new IllegalArgumentException("Null AUID");
    }

    String endpoint = String.format("%s/aus/%s/size", repositoryUrl, auid);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint)
        .queryParam("version", "all");

    if (namespace != null) {
      builder.queryParam("namespace", namespace);
    }

    ResponseEntity<String> response =
        RestUtil.callRestService(restTemplate,
            builder.build().encode().toUri(),
            HttpMethod.GET,
            new HttpEntity<>(null,
                getInitializedHttpHeaders()),
            String.class,
            "auSize");

    checkStatusOk(response);

    ObjectMapper objectMapper = new ObjectMapper();

    // Parse and return JSON response body as an AuSize bean
    return objectMapper.readValue(response.getBody(), AuSize.class);
  }

  /**
   * Returns information about the repository's storage areas
   *
   * @return A {@code RepositoryInfo}
   * @throws IOException if there are problems getting the repository data.
   */
  @Override
  public RepositoryInfo getRepositoryInfo() throws IOException {
    log.debug2("Invoked");
    String endpoint = String.format("%s/repoinfo", repositoryUrl);
    log.trace("endpoint = {}", endpoint);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "repoInfo");

      checkStatusOk(response);

      RepositoryInfo result = new ObjectMapper().readValue(response.getBody(),
          RepositoryInfo.class);
      log.debug2("result = {}", result);
      return result;
    } catch (LockssRestException e) {
      log.error("Could not fetch repository information", e);
      throw e;
    }
  }

  @Override
  public StorageInfo getStorageInfo() throws IOException {
    log.debug2("Invoked");
    String endpoint = String.format("%s/repoinfo/storage", repositoryUrl);
    log.trace("endpoint = {}", endpoint);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);

    try {
      ResponseEntity<String> response =
          RestUtil.callRestService(restTemplate,
              builder.build().encode().toUri(),
              HttpMethod.GET,
              new HttpEntity<>(null,
                  getInitializedHttpHeaders()),
              String.class,
              "repoInfo");

      checkStatusOk(response);

      StorageInfo result = new ObjectMapper().readValue(response.getBody(),
          StorageInfo.class);
      log.debug2("result = {}", result);
      return result;
    } catch (LockssRestException e) {
      log.error("Could not fetch repository information", e);
      throw e;
    }
  }

  // RestUtil.callRestService() throws on non-2xx response codes; this is a
  // sanity check to ensure that. */
  private void checkStatusOk(ResponseEntity resp) {
    checkStatusOk(resp.getStatusCode());
  }

  private void checkStatusOk(HttpStatusCode statusCode) {
    if (!statusCode.is2xxSuccessful()) {
      throw new RuntimeException("Shouldn't happen: RestUtil returned non-200 result");
    }
  }

  /**
   * Checks if the remote repository is alive.
   *
   * @return
   */
  private boolean checkAlive() {
    // TODO: Check Status API?
    return true;
  }

  /**
   * Returns a boolean indicating whether this repository is ready.
   *
   * @return
   */
  @Override
  public boolean isReady() {
    return checkAlive();
  }


  // ArtifactCache support.

  // Definitions for cache invalidate messages from repo service
  public static final String REST_ARTIFACT_CACHE_ID = "ArtifactCache";
  public static final String REST_ARTIFACT_CACHE_TOPIC = "ArtifactCacheTopic";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION = "CacheAction";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION_INVALIDATE_ARTIFACT =
      "InvalidateArt";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION_INVALIDATE_AU =
      "InvalidateAu";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION_FLUSH = "Flush";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION_ECHO = "Echo";
  public static final String REST_ARTIFACT_CACHE_MSG_ACTION_ECHO_RESP =
      "EchoResp";
  public static final String REST_ARTIFACT_CACHE_MSG_OP = "InvalidateOp";
  public static final String REST_ARTIFACT_CACHE_MSG_KEY = "ArtifactKey";

  // Artifact cache.  Disable by default; our client will enable if
  // desired
  private ArtifactCache artCache =
      new ArtifactCache(DEFAULT_MAX_ART_CACHE_SIZE,
          DEFAULT_MAX_ART_DATA_CACHE_SIZE)
          .enable(false);
  private JmsConsumer jmsConsumer;
  private JmsProducer jmsProducer;
  boolean isEnablingCache = false;
  boolean invalidateCheckCompleted = false;
  Deadline invCheckDeadline;

  /**
   * Enable the ArtifactCache
   *
   * @param enable true to enable
   * @param fact   JmsFactory to using to create a JMS consumer for cache
   *               invalidate messages
   * @return this
   */
  public RestLockssRepository enableArtifactCache(boolean enable,
                                                  JmsFactory fact) {
    if (enable) {
      synchronized (this) {
        if (!artCache.isEnabled() && !isEnablingCache) {
          makeJmsConsumer(fact);
        }
      }
    } else {
      artCache.enable(false);
    }
    return this;
  }

  /**
   * Return true if enableArtifactCache(true) has been called, whether or
   * not the cache has actually been enabled yet.
   */
  public boolean isArtifactCacheEnabled() {
    return isEnablingCache || artCache.isEnabled();
  }

  private void makeJmsConsumer(JmsFactory fact) {
    isEnablingCache = true;
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          log.debug("Creating JMS consumer");
          while (jmsConsumer == null || jmsProducer == null) {
            if (jmsConsumer == null) {
              try {
                log.trace("Attempting to create JMS consumer");
                jmsConsumer =
                    fact.createTopicConsumer(REST_ARTIFACT_CACHE_ID,
                        REST_ARTIFACT_CACHE_TOPIC,
                        new ArtifactCacheListener());
                log.debug("Created JMS consumer: {}", REST_ARTIFACT_CACHE_TOPIC);
              } catch (JMSException | NullPointerException exc) {
                log.trace("Could not establish JMS connection; sleeping and retrying");
              }
            }
            if (jmsProducer == null) {
              try {
                log.trace("Attempting to create JMS producer");
                jmsProducer =
                    fact.createTopicProducer(REST_ARTIFACT_CACHE_ID,
                        REST_ARTIFACT_CACHE_TOPIC);
                log.debug("Created JMS producer: {}",
                    REST_ARTIFACT_CACHE_TOPIC);
              } catch (JMSException | NullPointerException e) {
                log.error("Could not create JMS producer for {}",
                    REST_ARTIFACT_CACHE_ID, e);
              }
            }
            if (jmsConsumer != null && jmsProducer != null) {
              break;
            }
            TimerUtil.guaranteedSleep(1 * TimeUtil.SECOND);
          }
          // producer and consumer have been created, probe service with
          // ECHO request to determine whether it support sending JMS
          // cache invalidate messages, enable cache iff it responds.
          while (!invalidateCheckCompleted) {
            sendPing();
            invCheckDeadline = Deadline.in(5 * TimeUtil.SECOND);
            try {
              invCheckDeadline.sleep();
            } catch (InterruptedException e) {
              // ignore
            }
          }
          artCache.enable(true);
          log.info("Enabled Artifact cache");
        } finally {
          isEnablingCache = false;
        }
      }
    }).start();
  }

  protected void sendPing() {
    if (jmsProducer != null) {
      Map<String, Object> map = new HashMap<>();
      map.put(RestLockssRepository.REST_ARTIFACT_CACHE_MSG_ACTION,
          RestLockssRepository.REST_ARTIFACT_CACHE_MSG_ACTION_ECHO);
      map.put(RestLockssRepository.REST_ARTIFACT_CACHE_MSG_KEY,
          repositoryUrl.toString());
      try {
        jmsProducer.sendMap(map);
      } catch (JMSException e) {
        log.error("Couldn't send ping", e);
      }
    }
  }


  /**
   * @return the ArtifactCache
   */
  public ArtifactCache getArtifactCache() {
    return artCache;
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

  public void setUseMultipartEndpoint(boolean useMultipartEndpoint) {
    this.useMultipartEndpoint = useMultipartEndpoint;
  }

  //
  // This is here, rather than in ArtifactCache, to make ArtifactCache
  // independent of the particular notification mechanism.
  //
  private class ArtifactCacheListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
      try {
        Map<String, String> msgMap =
            (Map<String, String>) JmsUtil.convertMessage(message);
        String action = msgMap.get(REST_ARTIFACT_CACHE_MSG_ACTION);
        String key = msgMap.get(REST_ARTIFACT_CACHE_MSG_KEY);
        log.debug2("Received Artifact cache notification: {} key: {}",
            action, key);
        if (action != null) {
          switch (action) {
            case REST_ARTIFACT_CACHE_MSG_ACTION_INVALIDATE_ARTIFACT:
              artCache.invalidateArtifact(msgOp(msgMap.get(REST_ARTIFACT_CACHE_MSG_OP)),
                  key);
              break;
            case REST_ARTIFACT_CACHE_MSG_ACTION_INVALIDATE_AU:
              artCache.invalidateAu(msgOp(msgMap.get(REST_ARTIFACT_CACHE_MSG_OP)),
                  key);
              break;
            case REST_ARTIFACT_CACHE_MSG_ACTION_FLUSH:
              log.debug("Flushing Artifact cache");
              artCache.flush();
              break;
            case REST_ARTIFACT_CACHE_MSG_ACTION_ECHO_RESP:
              if (repositoryUrl.toString().equals(key)) {
                invalidateCheckCompleted = true;
                if (invCheckDeadline != null) {
                  invCheckDeadline.expire();
                }
                log.debug("invalidateCheckCompleted");
              }
              break;
            case REST_ARTIFACT_CACHE_MSG_ACTION_ECHO:
              // expected, ignore
              break;
            default:
              log.warn("Unknown message action: {}", action);
          }
        }
      } catch (JMSException | RuntimeException e) {
        log.error("Malformed Artifact cache message: {}", message, e);
      }
    }
  }

  ArtifactCache.InvalidateOp msgOp(String op) throws IllegalArgumentException {
    return Enum.valueOf(ArtifactCache.InvalidateOp.class, op);
  }

  /**
   * Provides a new set of HTTP headers including the Autorization header, if
   * necessary.
   *
   * @return an HttpHeaders with the HTTP headers.
   */
  private HttpHeaders getInitializedHttpHeaders() {
    HttpHeaders result = new HttpHeaders();

    // Check whether the Authorization header needs to be included.
    if (authHeaderValue != null) {
      // Yes.
      result.add("Authorization", authHeaderValue);
    }

    return result;
  }
}
