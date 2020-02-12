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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.ws.entities.PeerWsResult;
import org.lockss.ws.entities.PollWsResult;
import org.lockss.ws.entities.VoteWsResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * A client of the Poller REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestPollerClient extends RestBaseClient {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Constructor without authentication and with default timeouts.
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Poller web service.
   */
  public RestPollerClient(String serviceUrl) {
    super(serviceUrl);
  }

  /**
   * Constructor with authentication and default timeouts.
   * 
   * @param serviceUrl      A String with the information necessary to access
   *                        the REST Poller web service.
   * @param authHeaderValue A String with the Authorization header value to be
   *                        used, if any.
   */
  public RestPollerClient(String serviceUrl, String authHeaderValue) {
    super(serviceUrl, authHeaderValue);
  }

  /**
   * Constructor without authentication and with specified timeouts.
   * 
   * @param serviceUrl     A String with the information necessary to access the
   *                       REST Poller web service.
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   */
  public RestPollerClient(String serviceUrl, long connectTimeout,
      long readTimeout) {
    super(serviceUrl, connectTimeout, readTimeout);
  }

  /**
   * Constructor with authentication and specified timeouts.
   * 
   * @param serviceUrl      A String with the information necessary to access
   *                        the REST Poller web service.
   * @param authHeaderValue A String with the Authorization header value to be
   *                        used, if any.
   * @param connectTimeout  A long with the connection timeout in milliseconds.
   * @param readTimeout     A long with the read timeout in milliseconds.
   */
  public RestPollerClient(String serviceUrl, String authHeaderValue,
      long connectTimeout, long readTimeout) {
    super(serviceUrl, authHeaderValue, connectTimeout, readTimeout);
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
      log.debug2("result = " + result);
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
      ResponseEntity<String> response = callRestService("/peers", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query peers");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<PeerWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<PeerWsResult>>(){});

      log.debug2("result = " + result);
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
      ResponseEntity<String> response = callRestService("/polls", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query polls");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<PollWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<PollWsResult>>(){});

      log.debug2("result = " + result);
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
      ResponseEntity<String> response = callRestService("/votes", null,
	  queryParams, HttpMethod.GET, null, null, String.class,
	  "Can't query votes");
      log.trace("Back from RestUtil.callRestService");

      // Get the response body.
      List<VoteWsResult> result = getJsonMapper().readValue(response.getBody(),
	  new TypeReference<List<VoteWsResult>>(){});

      log.debug2("result = " + result);
      return result;
    } catch (Exception e) {
      throw new LockssRestException(e);
    }
  }
}
