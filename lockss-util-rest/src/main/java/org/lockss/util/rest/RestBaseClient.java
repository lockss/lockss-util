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
package org.lockss.util.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import org.lockss.log.L4JLogger;
import org.lockss.util.Constants;
import org.lockss.util.rest.exception.LockssRestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * A base client of a REST service.
 * 
 * @author Fernando García-Loygorri
 */
public class RestBaseClient {
  private static L4JLogger log = L4JLogger.getLogger();

  // Default timeouts.
  private static long defaultConnectTimeout = 10 * Constants.SECOND;
  private static long defaultReadTimeout = 30 * Constants.SECOND;

  // The URL of the REST web service.
  private String serviceUrl;

  // The Authorization header value to be used to access the REST web service.
  private String authHeaderValue;

  // The connection timeout, in milliseconds, to access the REST web service.
  private long connectTimeout;

  // The read timeout, in milliseconds, to access the REST web service.
  private long readTimeout;

  /**
   * Constructor without authentication and with default timeouts.
   * 
   * @param serviceUrl A String with the information necessary to access the
   *                   REST web service.
   */
  public RestBaseClient(String serviceUrl) {
    this(serviceUrl, null, defaultConnectTimeout, defaultReadTimeout);
  }

  /**
   * Constructor with authentication and default timeouts.
   * 
   * @param serviceUrl      A String with the information necessary to access
   *                        the REST web service.
   * @param authHeaderValue A String with the Authorization header value to be
   *                        used, if any.
   */
  public RestBaseClient(String serviceUrl, String authHeaderValue) {
    this(serviceUrl, authHeaderValue, defaultConnectTimeout,
	defaultReadTimeout);
  }

  /**
   * Constructor without authentication and with specified timeouts.
   * 
   * @param serviceUrl     A String with the information necessary to access the
   *                       REST web service.
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   */
  public RestBaseClient(String serviceUrl, long connectTimeout,
      long readTimeout) {
    this(serviceUrl, null, connectTimeout, readTimeout);
  }

  /**
   * Constructor with authentication and specified timeouts.
   * 
   * @param serviceUrl      A String with the information necessary to access
   *                        the REST web service.
   * @param authHeaderValue A String with the Authorization header value to be
   *                        used, if any.
   * @param connectTimeout  A long with the connection timeout in milliseconds.
   * @param readTimeout     A long with the read timeout in milliseconds.
   */
  public RestBaseClient(String serviceUrl, String authHeaderValue,
      long connectTimeout, long readTimeout) {
    this.serviceUrl = serviceUrl;
    this.authHeaderValue = authHeaderValue;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  /**
   * Makes a call to a REST service URI.
   * 
   * @param pathQuery        A String with the path and query parts of the REST
   *                         service endpoint.
   * @param uriVariables     A Map<String, String> with any variables to be
   *                         interpolated in the URI.
   * @param queryParams      A Map<String, String> with any query parameters.
   * @param httpMethod       An HttpMethod with HTTP method used to make the
   *                         call to the REST service.
   * @param requestHeaders   An HttpHeaders with any request headers (other than
   *                         the Authorization header) needed to make the
   *                         request, if any.
   * @param body             A T with the contents of the body to be included
   *                         with the request, if any.
   * @param exceptionMessage A String with the message to be returned with any
   *                         exception.
   * @return a ResponseEntity<String> with the response from the REST service.
   * @throws LockssRestException if any problems arise in the call to the REST
   *                             service.
   */
  protected <T, U> ResponseEntity<U> callRestService(String pathQuery,
      Map<String, String> uriVariables, Map<String, String> queryParams,
      HttpMethod httpMethod, HttpHeaders requestHeaders, T body,
      Class<U> responseType, String exceptionMessage)
	  throws LockssRestException {
    log.debug2("pathQuery = {}", pathQuery);
    log.debug2("uriVariables = {}", uriVariables);
    log.debug2("queryParams = {}", queryParams);
    log.debug2("httpMethod = {}", httpMethod);
    log.debug2("requestHeaders = {}", requestHeaders);
    log.debug2("body = {}", body);
    log.debug2("responseType = {}", responseType);
    log.debug2("exceptionMessage = {}", exceptionMessage);

    URI uri =
	RestUtil.getRestUri(serviceUrl + pathQuery, uriVariables, queryParams);
    log.trace("uri = {}", uri);

    HttpHeaders fullRequestHeaders = addAuthorizationHeader(requestHeaders);
    log.trace("fullRequestHeaders = {}", fullRequestHeaders);

    // Make the REST call.
    log.trace("Calling RestUtil.callRestService");
    return RestUtil.callRestService(RestUtil.getRestTemplate(connectTimeout,
	readTimeout), uri, httpMethod,
	new HttpEntity<T>(body, fullRequestHeaders), responseType,
	exceptionMessage);
  }

  /**
   * Provides a mapper that can be used to unmarshall JSON.
   * 
   * @return an ObjectMapper with the mapper that can be used to unmarshall
   *         JSON.
   */
  public ObjectMapper getJsonMapper() {
    return new ObjectMapper()
	.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Adds the Authorization header to a set of HTTP headers, if necessary.
   * 
   * @param requestHeaders An HttpHeaders with the original HTTP headers.
   * @return an HttpHeaders with the Authorization header added, if necessary.
   */
  private HttpHeaders addAuthorizationHeader(HttpHeaders requestHeaders) {
    log.debug2("requestHeaders = {}", requestHeaders);
    HttpHeaders fullRequestHeaders = requestHeaders;

    // Check whether there are credentials to be sent.
    if (authHeaderValue != null && !authHeaderValue.isEmpty()) {
      // Yes: Check whether no HTTP request headers were passed.
      if (fullRequestHeaders == null) {
	// Yes: Initialize the HTTP request headers.
	fullRequestHeaders = new HttpHeaders();
      }

      // Add the credentials to the request.
      fullRequestHeaders.set("Authorization", authHeaderValue);
    }

    log.debug2("fullRequestHeaders = {}", fullRequestHeaders);
    return fullRequestHeaders;
  }
}
