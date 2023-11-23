/*

Copyright (c) 2000-2023, Board of Trustees of Leland Stanford Jr. University

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

package org.lockss.util.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import javax.mail.MessagingException;
import org.lockss.log.L4JLogger;
import org.lockss.util.Constants;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.multipart.MultipartConnector;
import org.lockss.util.rest.multipart.MultipartResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for clients of REST services.
 * 
 * The generics magic in this class and subclasses allows subclass methods
 * to be chained from base class methods.  E.g.,<code>
 * new RestPollerClient().setTimeouts(t1, t2).callPoll(...);</code>
 *
 * @author Fernando García-Loygorri
 */
public class RestBaseClient<C extends RestBaseClient<?>> {
  private static L4JLogger log = L4JLogger.getLogger();

  // Default timeouts.
  private static long defaultConnectTimeout = 10 * Constants.SECOND;
  private static long defaultReadTimeout = 30 * Constants.SECOND;

  protected final C self;

  // The URL of the REST web service.
  private String serviceUrl;

  // Additional headers added by calling code
  HttpHeaders additionalReqHeaders;

  // The connect timeout, in milliseconds, to access the REST web service.
  private long connectTimeout = defaultConnectTimeout;

  // The read timeout, in milliseconds, to access the REST web service.
  private long readTimeout = defaultReadTimeout;
  private RestTemplate restTemplate;

  protected RestBaseClient(final Class<C> selfClass) {
    this.self = selfClass.cast(this);
  }

  public C setServiceUrl(String url) {
    this.serviceUrl = url;
    return self;
  }

  /** Set the connect and read timeouts */
  public C setTimeouts(long connectTimeout, long readTimeout) {
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    return self;
  }

  public C setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    return self;
  }

  private RestTemplate getRestTemplate() {
    if (this.restTemplate == null) {
      this.restTemplate = RestUtil.getRestTemplate(connectTimeout, readTimeout);
    }
    return this.restTemplate;
  }

  /** Add the headers to the request headers, replacing any that already
   * have a value */
  public C addRequestHeaders(HttpHeaders headers) {
    additionalReqHeaders =
      SpringHeaderUtil.addHeaders(headers, additionalReqHeaders, true);
    log.debug2("Additional headers: {}", additionalReqHeaders);
    return self;
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
   * @param requestHeaders An HttpHeaders with any request headers needed
   *                         to make the request, if any.
   * @param body             A T with the contents of the body to be included
   *                         with the request, if any.
   * @param exceptionMessage A String with the message to be returned with any
   *                         exception.
   * @return a ResponseEntity<U> with the response from the REST service.
   * @throws LockssRestException if any problems arise in the call to the REST
   *                             service.
   */
  protected <T, U> ResponseEntity<U> callRestService(String pathQuery,
      Map<String, String> uriVariables, Map<String, String> queryParams,
      HttpMethod httpMethod, HttpHeaders requestHeaders, T body,
      Class<U> responseType, String exceptionMessage)
	  throws LockssRestException {
    return callRestService(pathQuery,
                           uriVariables, queryParams,
                           httpMethod, requestHeaders, body,
                           responseType, exceptionMessage, null);
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
   * @param requestHeaders An HttpHeaders with any request headers needed
   *                         to make the request, if any.
   * @param body             A T with the contents of the body to be included
   *                         with the request, if any.
   * @param exceptionMessage A String with the message to be returned with any
   *                         exception.
   * @param retryBackoffs    An array of longs specifying successive intervals
   *                         to wait between successive retries.  If null or
   *                         empty, no retries.
   * @return a ResponseEntity<U> with the response from the REST service.
   * @throws LockssRestException if any problems arise in the call to the REST
   *                             service.
   */
  protected <T, U> ResponseEntity<U> callRestService(String pathQuery,
      Map<String, String> uriVariables, Map<String, String> queryParams,
      HttpMethod httpMethod, HttpHeaders requestHeaders, T body,
      Class<U> responseType, String exceptionMessage,
      long[] retryBackoffs)
	  throws LockssRestException {
    log.debug2("pathQuery = {}", pathQuery);
    log.debug2("uriVariables = {}", uriVariables);
    log.debug2("queryParams = {}", queryParams);
    log.debug2("httpMethod = {}", httpMethod);
    log.debug2("requestHeaders = {}", requestHeaders);
    log.debug2("body = {}", body);
    log.debug2("responseType = {}", responseType);
    log.debug2("exceptionMessage = {}", exceptionMessage);
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    URI uri =
	RestUtil.getRestUri(serviceUrl + pathQuery, uriVariables, queryParams);
    log.trace("uri = {}", uri);

    HttpHeaders fullRequestHeaders =
      SpringHeaderUtil.addHeaders(requestHeaders, additionalReqHeaders);
    log.trace("fullRequestHeaders = {}", fullRequestHeaders);

    // Make the REST call.
    log.trace("Calling RestUtil.callRestService");
    return RestUtil.callRestService(getRestTemplate(), uri, httpMethod,
	new HttpEntity<T>(body, fullRequestHeaders), responseType,
        exceptionMessage, retryBackoffs);
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
   * Makes a call to a REST service endpoint that returns a multi-part response.
   * 
   * @param pathQuery        A String with the path and query parts of the REST
   *                         service endpoint.
   * @param uriVariables     A Map<String, String> with any variables to be
   *                         interpolated in the URI.
   * @param queryParams      A Map<String, String> with any query parameters.
   * @param requestHeaders   An HttpHeaders with HTTP request headers used to
   *                         make the call to the REST service.
   * @param httpMethod       An HttpMethod with HTTP method used to make the
   *                         call to the REST service.
   * @param body             A T with the contents of the body to be included
   *                         with the request, if any.
   * @return a MultipartResponse with the response from the REST service.
   * @throws LockssRestException if any problems arise in the call to the REST
   *                             service.
   */
  protected <T> MultipartResponse getMultipartResponse(String pathQuery,
      Map<String, String> uriVariables, Map<String, String> queryParams,
      HttpHeaders requestHeaders, HttpMethod httpMethod, T body)
	  throws IOException, MessagingException {
    log.debug2("pathQuery = {}", pathQuery);
    log.debug2("uriVariables = {}", uriVariables);
    log.debug2("queryParams = {}", queryParams);
    log.debug2("requestHeaders = {}", requestHeaders);
    log.debug2("httpMethod = {}", httpMethod);
    log.debug2("body = {}", body);

    URI uri = RestUtil.getRestUri(serviceUrl + pathQuery, uriVariables,
	queryParams);
    log.trace("uri = {}", uri);

    requestHeaders.setAccept(Arrays.asList(MediaType.MULTIPART_FORM_DATA,
	MediaType.APPLICATION_JSON));

    HttpHeaders fullRequestHeaders =
      SpringHeaderUtil.addHeaders(requestHeaders, additionalReqHeaders);
    log.trace("fullRequestHeaders = {}", fullRequestHeaders);

    // Make the REST call.
    log.trace("Calling MultipartConnector.requestGet");
    return new MultipartConnector(uri, fullRequestHeaders).request(getRestTemplate(), httpMethod,
	body, (int)connectTimeout, (int)readTimeout);
  }
}
