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
package org.lockss.util.rest.config;

import java.util.HashMap;
import java.util.Map;
import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestBaseClient;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.ws.entities.CheckSubstanceResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * A client of the Configuration REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestConfigClient extends RestBaseClient {
  private static final String X_LOCKSS_REQUEST_COOKIE_NAME =
      "X-Lockss-Request-Cookie";

  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Constructor without authentication and with default timeouts.
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST Poller web service.
   */
  public RestConfigClient(String serviceUrl) {
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
  public RestConfigClient(String serviceUrl, String authHeaderValue) {
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
  public RestConfigClient(String serviceUrl, long connectTimeout,
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
  public RestConfigClient(String serviceUrl, String authHeaderValue,
      long connectTimeout, long readTimeout) {
    super(serviceUrl, authHeaderValue, connectTimeout, readTimeout);
  }

  /**
   * Stores the state of an Archival Unit via the REST web service.
   * 
   * @param auId                 A String with the Archival Unit identifier.
   * @param auState              A String with the Archival Unit state.
   * @param xLockssRequestCookie A String with the request cookie.
   * @return a String with the response of the REST request.
   * @throws LockssRestException if there are problems updating the Archival
   *                             Unit state.
   */
  public String patchArchivalUnitState(String auId, String auState,
      String xLockssRequestCookie) throws LockssRestException {
    log.debug2("auId = {}", auId);
    log.debug2("auState = {}", auState);
    log.debug2("xLockssRequestCookie = {}", xLockssRequestCookie);

    // Prepare the URI path variables.
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("auid", auId);
    log.trace("uriVariables = {}", uriVariables);

    // Initialize the request headers.
    HttpHeaders requestHeaders = new HttpHeaders();

    // Set the content type.
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);

    // Set the request cookie, if passed.
    if (xLockssRequestCookie != null) {
      requestHeaders.set(X_LOCKSS_REQUEST_COOKIE_NAME, xLockssRequestCookie);
    }

    // Make the REST call.
    ResponseEntity<String> response = callRestService("/austates/{auid}",
	uriVariables, null, HttpMethod.PATCH, requestHeaders, auState,
	String.class, "Cannot update AU state");
    log.trace("Back from RestUtil.callRestService");

    String result = response.getBody();
    log.debug2("result = {}", result);
    return result;
  }

  /**
   * Updates the substance check of an archival unit in the system.
   * 
   * @param auId A String with the identifier of the archival unit.
   * @return a CheckSubstanceResult with the substance check information of the
   *         archival unit.
   * @throws LockssRestException if there are problems updating the Archival
   *                             Unit substance.
   */
  public CheckSubstanceResult putAuSubstanceCheck(String auId) 
      throws LockssRestException {
    log.debug2("auId = {}", auId);

    // Prepare the URI path variables.
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("auid", auId);
    log.trace("uriVariables = {}", uriVariables);

    // Make the REST call.
    ResponseEntity<CheckSubstanceResult> response = callRestService(
	"/ausubstances/{auid}", uriVariables, null, HttpMethod.PUT, null, null,
	CheckSubstanceResult.class, "Cannot update AU substance");
    log.trace("Back from RestUtil.callRestService");

    CheckSubstanceResult result = response.getBody();
    log.debug2("result = {}", result);
    return result;
  }
}
