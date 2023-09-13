/*

Copyright (c) 2020 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.lockss.util.rest.poller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.cxf.attachment.AttachmentDataSource;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.multipart.MultipartResponse;
import org.lockss.util.rest.multipart.MultipartResponse.Part;
import org.lockss.ws.entities.*;
import org.springframework.http.*;

import jakarta.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.lockss.ws.entities.HasherWsResult.BLOCK_FILE_TYPE;
import static org.lockss.ws.entities.HasherWsResult.RECORD_FILE_TYPE;

/**
 * A client of the Poller REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestPollerClient extends RestBaseClient<RestPollerClient> {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Constructor
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Poller web service.
   */
  public RestPollerClient(String serviceUrl) {
    super(RestPollerClient.class);
    setServiceUrl(serviceUrl);
  }

  /**
   * Sends to the poller REST service a request to call a poll.
   * 
   * @param body A PollDesc with the description of the poll to be called.
   * @return a String with the AUID of the Archival Unit involved in the poll.
   * @throws LockssRestException if there were problems calling the poll.
   */
  public String callPoll(PollDesc body) throws LockssRestException {
    log.debug2("body = {}", body);

    try {
      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/polls", null, null,
	  HttpMethod.POST, null, body, String.class, "Can't call poll");
      log.trace("Back from RestUtil.callRestService");

      String result = response.getBody();
      log.debug2("result = {}", result);
      return result;
    } catch (RuntimeException e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected peers in the system.
   * 
   * @param peerQuery A String with the
   *                  <a href="package-summary.html#SQL-Like_Query">SQL-like
   *                  query</a> used to specify what properties to retrieve from
   *                  which peers.
   * @return a {@code List<PeerWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<PeerWsResult> queryPeers(String peerQuery)
      throws LockssRestException {
    log.debug2("peerQuery = {}", peerQuery);

    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("peerQuery", peerQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/ws/peers", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query peers");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<PeerWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<PeerWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected polls in the system.
   * 
   * @param pollQuery A String with the
   *                  <a href="package-summary.html#SQL-Like_Query">SQL-like
   *                  query</a> used to specify what properties to retrieve from
   *                  which polls.
   * @return a {@code List<PollWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<PollWsResult> queryPolls(String pollQuery)
      throws LockssRestException {
    log.debug2("pollQuery = {}", pollQuery);

    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("pollQuery", pollQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/ws/polls", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query polls");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<PollWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<PollWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected votes in the system.
   * 
   * @param voteQuery A String with the
   *                  <a href="package-summary.html#SQL-Like_Query">SQL-like
   *                  query</a> used to specify what properties to retrieve from
   *                  which votes.
   * @return a {@code List<VoteWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<VoteWsResult> queryVotes(String voteQuery)
      throws LockssRestException {
    log.debug2("voteQuery = {}", voteQuery);

    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("voteQuery", voteQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/ws/votes", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query votes");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<VoteWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<VoteWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected repositories in the system.
   * 
   * @param repositoryQuery A String with the
   *                        <a href="package-summary.html#SQL-Like_Query">SQL-like
   *                        query</a> used to specify what properties to
   *                        retrieve from which repositories.
   * @return a {@code List<RepositoryWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<RepositoryWsResult> queryAuRepositories(String repositoryQuery)
      throws LockssRestException {
    log.debug2("repositoryQuery = {}", repositoryQuery);

    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("repositoryQuery", repositoryQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/ws/aurepositories", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query repositories");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<RepositoryWsResult> result = getJsonMapper()
	  .readValue(response.getBody(),
	  new TypeReference<List<RepositoryWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the selected properties of selected repository spaces in the
   * system.
   * 
   * @param repositorySpaceQuery A String with the <a href=
   *                             "package-summary.html#SQL-Like_Query">SQL-like
   *                             query</a> used to specify what properties to
   *                             retrieve from which repository spaces.
   * @return a {@code List<RepositorySpaceWsResult>} with the results.
   * @throws LockssRestException if there were problems making the query.
   */
  public List<RepositorySpaceWsResult> queryRepositorySpaces(
      String repositorySpaceQuery) throws LockssRestException {
    log.debug2("repositorySpaceQuery = {}", repositorySpaceQuery);

    try {
      // Prepare the query parameters.
      Map<String, String> queryParams = new HashMap<>(1);
      queryParams.put("repositorySpaceQuery", repositorySpaceQuery);
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      log.trace("Calling RestUtil.callRestService");
      ResponseEntity<String> response = callRestService("/ws/repositoryspaces",
	  null, queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query repository spaces");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<RepositorySpaceWsResult> result = getJsonMapper().
	  readValue(response.getBody(),
	  new TypeReference<List<RepositorySpaceWsResult>>(){});

      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Performs the hashing of an AU or a URL.
   * 
   * @param wsParams A HasherWsParams with the parameters of the hashing
   *                 operation.
   * @return a HasherWsResult with the result of the hashing operation.
   * @throws LockssRestException if there are problems.
   */
  public HasherWsResult hash(HasherWsParams wsParams)
      throws LockssRestException {
    log.debug2("wsParams = {}", wsParams);

    try {
      // Prepare the query parameter variables.
      Map<String, String> queryParams = new HashMap<>();
      queryParams.put("isAsynchronous", "false");
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      MultipartResponse response = getMultipartResponse("/ws/hashes", null,
	  queryParams, new HttpHeaders(), HttpMethod.PUT, wsParams);

      // Get the single hash result from the response.
      HasherWsResult result = extractSingleHashResult(response);
      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Performs asynchronously the hashing of an AU or a URL.
   * 
   * @param wsParams A HasherWsParams with the parameters of the hashing
   *                 operation.
   * @return a HasherWsAsynchronousResult with the result of the hashing
   *         operation.
   * @throws LockssRestException if there are problems.
   */
  public HasherWsAsynchronousResult hashAsynchronously(HasherWsParams wsParams)
      throws LockssRestException {
    log.debug2("wsParams = {}", wsParams);

    try {
      // Prepare the query parameter variables.
      Map<String, String> queryParams = new HashMap<>();
      queryParams.put("isAsynchronous", "true");
      log.trace("queryParams = {}", queryParams);

      // Make the REST call.
      MultipartResponse response = getMultipartResponse("/ws/hashes", null, queryParams,
	  new HttpHeaders(), HttpMethod.PUT, wsParams);

      // Get the single hash result from the response.
      HasherWsAsynchronousResult result = extractSingleHashResult(response);
      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the result of an asynchronous hashing operation.
   * 
   * @param requestId A String with the identifier of the requested asynchronous
   *                  hashing operation.
   * @return a HasherWsAsynchronousResult with the result of the hashing
   *         operation.
   * @throws LockssRestException if there are problems.
   */
  public HasherWsAsynchronousResult getAsynchronousHashResult(String requestId)
      throws LockssRestException {
    log.debug2("requestId = {}", requestId);

    try {
      // Prepare the URI path variables.
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("requestId", requestId);
      log.trace("uriVariables = {}", uriVariables);

      // Make the REST call.
      MultipartResponse response = getMultipartResponse(
	  "/ws/hashes/requests/{requestId}", uriVariables, null, new HttpHeaders(),
	  HttpMethod.GET, null);

      // Get the single hash result from the response.
      HasherWsAsynchronousResult result = extractSingleHashResult(response);
      log.debug2("result = {}", result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Provides the results of all the asynchronous hashing operations.
   * 
   * @return a {@code List<HasherWsAsynchronousResult>} with the results of the
   *         hashing operations.
   * @throws LockssRestException if there are problems.
   */
  public List<HasherWsAsynchronousResult> getAllAsynchronousHashResults()
      throws LockssRestException {
    log.debug2("Invoked.");

    try {
      // Make the REST call.
      MultipartResponse response = getMultipartResponse("/ws/hashes", null, null,
	  new HttpHeaders(), HttpMethod.GET, null);

      // Get all the hash results from the response.
      List<HasherWsAsynchronousResult> wsResults =
	  extractAllHashResults(response);
      log.debug2("wsResults = {}", wsResults);
      return wsResults;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }

  /**
   * Removes from the system an asynchronous hashing operation, terminating it
   * if it's still running.
   * 
   * @param requestId A String with the identifier of the requested asynchronous
   *                  hashing operation.
   * @return a HasherWsAsynchronousResult with the result of the removal of the
   *         hashing operation.
   * @throws LockssRestException if there are problems.
   */
  public HasherWsAsynchronousResult removeAsynchronousHashRequest(String
      requestId) throws LockssRestException {
    log.debug2("requestId = {}", requestId);

    // Populate the result.
    HasherWsAsynchronousResult result = new HasherWsAsynchronousResult();
    result.setRequestId(requestId);

    try {
      // Prepare the URI path variables.
      Map<String, String> uriVariables = new HashMap<>();
      uriVariables.put("requestId", requestId);
      log.trace("uriVariables = {}", uriVariables);

      // Make the REST call.
      ResponseEntity<String> response = callRestService(
	  "/ws/hashes/requests/{requestId}", uriVariables, null, HttpMethod.DELETE,
	  null, (Void)null, String.class, "Can't remove asynchronous hash");

      // Get the response body.
      String responseBody = response.getBody();
      log.trace("responseBody = {}", responseBody);

      result.setStatus(responseBody);
      log.debug2("result = {}", result);
    } catch (LockssRestHttpException e) {
      switch (e.getHttpStatus()) {
        case BAD_REQUEST:
        case NOT_FOUND:
        case INTERNAL_SERVER_ERROR:
          // Pass-through error message in response body
          result.setStatus("RequestError"); // HasherStatus.RequestError.toString()
          result.setErrorMessage(e.getServerErrorMessage());
          break;

        default:
          throw new LockssRestException(e);
      }
    } catch (Exception e) {
      throw new LockssRestException(e);
    }

    return result;
  }

  /**
   * Extracts a single hash result from a multi-part response.
   * 
   * @param response A MultipartResponse from where to extract the hash result.
   * @return an HasherWsAsynchronousResult with the extracted hash result.
   * @throws IOException if there are problems getting the hash results from the
   *                     response.
   */
  private HasherWsAsynchronousResult extractSingleHashResult(
      MultipartResponse response) throws IOException {
    log.debug2("Invoked");

    // Get all the results from the response.
    List<HasherWsAsynchronousResult> wsResults =
	extractAllHashResults(response);

    // Check whether a single hash result was included the response.
    if (wsResults.size() == 1) {
      // Yes: Return it.
      HasherWsAsynchronousResult result = wsResults.get(0);
      log.debug2("result = {}", result);
      return result;
    } else {
      // No: report the problem.
      String message = "REST service returned " + wsResults.size()
      + " hash results instead of a single hash result";
      log.error(message);
      throw new RuntimeException(message);
    }
  }

  /**
   * Extracts all hash results from a multi-part response.
   * 
   * @param response A MultipartResponse from where to extract all the hash
   *                 results.
   * @return a {@code List<HasherWsAsynchronousResult>} with the extracted hash
   *         results.
   * @throws IOException if there are problems getting the hash results from the
   *                     response.
   */
  private List<HasherWsAsynchronousResult> extractAllHashResults(
      MultipartResponse response) throws IOException {
    log.debug2("Invoked");

    // Get the status code included in the response.
    HttpStatus statusCode = response.getStatusCode();
    log.trace("statusCode = {}", statusCode);

    // Check whether the operation indicates success.
    if (statusCode.equals(HttpStatus.OK)) {
      // Yes: Get the results.
      List<HasherWsAsynchronousResult> wsResults = getHasherResults(response);
      log.debug2("wsResults = {}", wsResults);
      return wsResults;
    } else {
      // No: report the problem.
      String message = "REST service returned statusCode '" + statusCode
	  + ", statusMessage = '" + response.getStatusMessage() + "'";

      log.error(message);
      throw new RuntimeException(message);
    }
  }

  /**
   * Provides the hash results included in a multi-part response.
   * 
   * @param response A MultipartResponse from where to get the hash results.
   * @return a {@code List<HasherWsAsynchronousResult>} with the extracted hash
   *         results.
   * @throws IOException if there are problems getting the hash results from the
   *                     response.
   */
  private List<HasherWsAsynchronousResult> getHasherResults(
      MultipartResponse response) throws IOException {
    log.debug2("Invoked");

    // Maps, keyed by requestId, of the different types of part contents.
    Map<String, HasherWsAsynchronousResult> hwarMap = new HashMap<>();
    Map<String, AttachmentDataSource> blockFileMap = new HashMap<>();
    Map<String, AttachmentDataSource> recordFileMap = new HashMap<>();

    // Get the response parts.
    Map<String, Part> parts = response.getParts();
    log.trace("parts = {}", parts);

    int partCount = parts.size();
    log.trace("partCount = {}", partCount);

    // Loop through all the response parts.
    for (String partName : parts.keySet()) {
      log.trace("partName = {}", partName);

      // Get the requestId and file type corresponding to this part.
      String requestId = partName;
      String fileType = null;

      int separatorLoc = partName.indexOf("-");
      log.trace("separatorLoc = {}", separatorLoc);

      if (separatorLoc > 0) {
	requestId = partName.substring(0, separatorLoc);
	fileType = partName.substring(separatorLoc + 1);
      }

      log.trace("requestId = {}", requestId);
      log.trace("fileType = {}", fileType);

      // Get the part.
      Part part = parts.get(partName);
      log.trace("part = {}", part);

      // Get the part content type.
      String contentType = part.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
      log.trace("contentType = {}", contentType);

      // Check whether this part does not involve a file and it's JSON.
      if (fileType == null
	  && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
	// Yes.
	try (InputStream partInputStream = part.getInputStream();
	    Scanner s = new Scanner(partInputStream).useDelimiter("\\A")) {
	  // Read the part content.
	  String result = s.hasNext() ? s.next() : "";
	  log.trace("result = {}", result);

	  // Parse it as a hash result.
	  HasherWsAsynchronousResult hwar = getJsonMapper().readValue(result,
	      HasherWsAsynchronousResult.class);
	  log.trace("hwar = {}", hwar);

	  // Add the hash result to the appropriate map.
	  hwarMap.put(requestId, hwar);
	}
	// No: Check whether this part involves a file.
      } else if (fileType != null) {
	// Yes: Get its data source.
	AttachmentDataSource source =
	    new AttachmentDataSource(contentType, part.getInputStream());
	log.trace("source = {}", source);

	// Check whether this part involves a block file.
	if (fileType.equals(BLOCK_FILE_TYPE)) {
	  // Yes: Add its data source to the appropriate map.
	  blockFileMap.put(requestId, source);
	  // No: Check whether this part involves a record file.
	} else if (fileType.equals(RECORD_FILE_TYPE)) {
	  // Yes: Add its data source to the appropriate map.
	  recordFileMap.put(requestId, source);
	} else {
	  log.warn("Ignoring unexpected part with contentType = '{}', "
	      + "fileType = '{}'", contentType, fileType);
	}
      } else {
	log.warn("Ignoring unexpected part with contentType = '{}', "
	    + "fileType = '{}'", contentType, fileType);
      }

      log.trace("Done processing part with name '{}'", partName);
    }

    // Initialize the response.
    List<HasherWsAsynchronousResult> wsResults =
	new ArrayList<HasherWsAsynchronousResult>();

    // Loop through all the requestIds of hash results included in the response.
    for (String requestId : hwarMap.keySet()) {
      log.trace("requestId = {}", requestId);

      // Get the hash result.
      HasherWsAsynchronousResult wsResult = hwarMap.get(requestId);

      // Populate the appropriate block file data source, if any.
      AttachmentDataSource blockFileDataSource = blockFileMap.get(requestId);
      log.trace("blockFileDataSource == null? {}", blockFileDataSource == null);

      if (blockFileDataSource != null) {
	wsResult.setBlockFileDataHandler(new DataHandler(blockFileDataSource));
      }

      // Populate the appropriate record file data source, if any.
      AttachmentDataSource recordFileDataSource = recordFileMap.get(requestId);
      log.trace("recordFileDataSource == null? {}",
	  recordFileDataSource == null);

      if (recordFileDataSource != null) {
	wsResult.setRecordFileDataHandler(new DataHandler(
	    recordFileDataSource));
      }

      log.trace("wsResult = {}", wsResult);

      // Add this hash result to the overall results.
      wsResults.add(wsResult);
    }

    log.debug2("wsResults = {}", wsResults);
    return wsResults;
  }
}
