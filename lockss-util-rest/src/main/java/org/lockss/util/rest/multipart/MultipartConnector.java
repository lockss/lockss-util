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

package org.lockss.util.rest.multipart;

import org.lockss.log.L4JLogger;
import org.lockss.util.Constants;
import org.lockss.util.rest.HttpResponseStatusAndHeaders;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.exception.LockssRestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Utility to simplify calling a REST service that operates on HTTP Multipart
 * objects.
 */
public class MultipartConnector {
  private static L4JLogger log = L4JLogger.getLogger();

  private final long DEFAULT_TIMEOUT = 60 * Constants.SECOND;

  private URI uri;
  private HttpHeaders requestHeaders;
  private MultiValueMap<String, Object> parts;
  private RestTemplate restTemplate;

  /**
   * Constructor for GET operations.
   *
   * @param uri
   *          A URI defining the REST service operation to be called.
   * @param requestHeaders
   *          An HttpHeaders with the headers of the request to be made.
   */
  public MultipartConnector(URI uri, HttpHeaders requestHeaders) {
    this.uri = uri;
    this.requestHeaders = requestHeaders;
    this.parts = null;
  }

  /**
   * Constructor for PUT operations.
   *
   * @param uri
   *          A URI defining the REST service operation to be called.
   * @param requestHeaders
   *          An HttpHeaders with the headers of the request to be made.
   * @param parts
   *          A MultiValueMap<String, Object> with the multipart object parts.
   */
  public MultipartConnector(URI uri, HttpHeaders requestHeaders,
      MultiValueMap<String, Object> parts) {
    this.uri = uri;
    this.requestHeaders = requestHeaders;
    this.parts = parts;
  }

  /**
   * Performs the GET request.
   *
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public MultipartResponse requestGet() throws IOException, MessagingException {
    return requestGet(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
  }

  /**
   * Performs the GET request.
   *
   * @param connectTimeout
   *          A long with the connection timeout in ms.
   * @param readTimeout
   *          A long with the read timeout in ms.
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public MultipartResponse requestGet(long connectTimeout, long readTimeout)
      throws IOException, MessagingException {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    // Initialize the request to the REST service.
    RestTemplate restTemplate =
        this.restTemplate == null ?
            createRestTemplate(connectTimeout, readTimeout) : this.restTemplate;

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making GET request to '{}'...", uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<MultipartMessage> response =
	  RestUtil.callRestService(restTemplate, uri, HttpMethod.GET,
	      new HttpEntity<>(null, requestHeaders), MultipartMessage.class,
	      "Cannot get MultipartMessage object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new MultipartResponse(response);
    } catch (LockssRestException lre) {
      log.debug2("Exception caught getting MultipartMessage object", lre);
      log.debug2("uri = {}", uri);
      log.debug2("requestHeaders = {}", requestHeaders.toSingleValueMap());
      return new MultipartResponse(lre);
    } catch (IOException e) {
      log.error("Exception caught getting MultipartMessage object", e);
      log.error("uri = {}", uri);
      log.error("requestHeaders = {}", requestHeaders.toSingleValueMap());
      throw e;
    }
  }

  /**
   * Creates the REST template for the request.
   *
   * @param connectTimeout
   *          A long with the connection timeout in ms.
   * @param readTimeout
   *          A long with the read timeout in ms.
   * @return a RestTemplate with the REST template for the request.
   */
  private RestTemplate createRestTemplate(long connectTimeout,
      long readTimeout) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    // Initialize the request to the REST service.
    RestTemplate restTemplate =
	RestUtil.getRestTemplate(connectTimeout, readTimeout);

    // Get the current message converters.
    List<HttpMessageConverter<?>> messageConverters =
	restTemplate.getMessageConverters();
    log.trace("messageConverters = {}", messageConverters);

    // Add the multipart/form-data converter.
    messageConverters.add(new MultipartMessageHttpMessageConverter());
    log.trace("messageConverters = {}", messageConverters);

    return restTemplate;
  }

  /**
   * Performs the PUT request.
   *
   * @return an HttpStatus with the response status.
   */
  public HttpResponseStatusAndHeaders requestPut() {
    return requestPut(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
  }

  /**
   * Performs the PUT request.
   *
   * @param connectTimeout
   *          A long with the connection timeout in ms.
   * @param readTimeout
   *          A long with the read timeout in ms.
   * @return an HttpStatus with the response status.
   */
  public HttpResponseStatusAndHeaders requestPut(long connectTimeout,
      long readTimeout) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    // Initialize the request to the REST service.
    RestTemplate restTemplate =
        this.restTemplate == null ?
            createRestTemplate(connectTimeout, readTimeout) : this.restTemplate;

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making PUT request to '{}'...", uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<?> response = RestUtil.callRestService(restTemplate, uri,
	  HttpMethod.PUT,
	  new HttpEntity<MultiValueMap<String, Object>>(parts, requestHeaders),
	  Void.class, "Cannot update MultipartMessage object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new HttpResponseStatusAndHeaders(response.getStatusCodeValue(),
	  null, response.getHeaders());
    } catch (LockssRestException lre) {
      log.debug2("Exception caught updating MultipartMessage object", lre);
      log.debug2("uri = {}", uri);
      log.debug2("requestHeaders = {}", requestHeaders.toSingleValueMap());
      return HttpResponseStatusAndHeaders.fromLockssRestException(lre);
    }
  }

  /**
   * Performs a request that results in a multi-part response.
   *
   * @param method
   *          An HttpMethod with the method of the request to the REST service.
   * @param body
   *          A T with the body of the request, if any.
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public <T> MultipartResponse request(HttpMethod method, T body)
      throws IOException, MessagingException {
    return request(method, body, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
  }

  /**
   * Performs a request that results in a multi-part response.
   *
   * @param restTemplate The {@link RestTemplate} client to use for REST calls.
   * @param method
   *          An HttpMethod with the method of the request to the REST service.
   * @param body
   *          A T with the body of the request, if any.
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public <T> MultipartResponse request(RestTemplate restTemplate, HttpMethod method, T body)
      throws IOException, MessagingException {
    return request(restTemplate, method, body, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
  }

  /**
   * Performs a request that results in a multi-part response.
   *
   * @param httpMethod
   *          An HttpMethod with the method of the request to the REST service.
   * @param body
   *          A T with the body of the request, if any.
   * @param connectTimeout
   *          A long with the connection timeout in ms.
   * @param readTimeout
   *          A long with the read timeout in ms.
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public <T> MultipartResponse request(HttpMethod httpMethod, T body,
      long connectTimeout, long readTimeout)
      throws IOException, MessagingException {

    // Initialize the request to the REST service.
    RestTemplate restTemplate =
        this.restTemplate == null ?
            createRestTemplate(connectTimeout, readTimeout) : this.restTemplate;

    return request(restTemplate, httpMethod, body, connectTimeout, readTimeout);
  }

  /**
   * Performs a request that results in a multi-part response.
   *
   * @param restTemplate The {@link RestTemplate} client to use for REST calls.
   * @param httpMethod
   *          An HttpMethod with the method of the request to the REST service.
   * @param body
   *          A T with the body of the request, if any.
   * @param connectTimeout
   *          A long with the connection timeout in ms.
   * @param readTimeout
   *          A long with the read timeout in ms.
   * @return a MultipartResponse with the response.
   * @throws IOException
   *           if there are problems getting a part payload.
   * @throws MessagingException
   *           if there are other problems.
   */
  public <T> MultipartResponse request(RestTemplate restTemplate, HttpMethod httpMethod, T body,
                                       long connectTimeout, long readTimeout)
	  throws IOException, MessagingException {
    log.debug2("httpMethod = {}", httpMethod);
    log.debug2("body = {}", body);
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making {} request to '{}'...", httpMethod, uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<MultipartMessage> response =
	  RestUtil.callRestService(restTemplate, uri, httpMethod,
	      new HttpEntity<>(body, requestHeaders), MultipartMessage.class,
	      "Cannot get MultipartMessage object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new MultipartResponse(response);
    } catch (LockssRestException lre) {
      log.debug2("Exception caught getting MultipartMessage object", lre);
      log.debug2("uri = {}", uri);
      log.debug2("requestHeaders = {}", requestHeaders.toSingleValueMap());
      return new MultipartResponse(lre);
    } catch (IOException e) {
      log.error("Exception caught getting MultipartMessage object", e);
      log.error("uri = {}", uri);
      log.error("requestHeaders = {}", requestHeaders.toSingleValueMap());
      throw e;
    }
  }

  public MultipartConnector setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    return this;
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
