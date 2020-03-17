/*

 Copyright (c) 2017-2020 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */
package org.lockss.util.rest.multipart;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import org.lockss.util.Constants;
import org.lockss.util.rest.HttpResponseStatusAndHeaders;
import org.lockss.util.rest.RestUtil;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.log.L4JLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate = createRestTemplate(connectTimeout, readTimeout);

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making GET request to '{}'...", uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<MimeMultipart> response =
	  RestUtil.callRestService(restTemplate, uri, HttpMethod.GET,
	      new HttpEntity<>(null, requestHeaders), MimeMultipart.class,
	      "Cannot get MimeMultipart object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new MultipartResponse(response);
    } catch (LockssRestException lre) {
      log.debug2("Exception caught getting MimeMultipart object", lre);
      log.debug2("uri = {}", uri);
      log.debug2("requestHeaders = {}", requestHeaders.toSingleValueMap());
      return new MultipartResponse(lre);
    } catch (IOException | MessagingException e) {
      log.error("Exception caught getting MimeMultipart object", e);
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
    messageConverters.add(new MimeMultipartHttpMessageConverter());
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
    RestTemplate restTemplate = createRestTemplate(connectTimeout, readTimeout);

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making PUT request to '{}'...", uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<?> response = RestUtil.callRestService(restTemplate, uri,
	  HttpMethod.PUT,
	  new HttpEntity<MultiValueMap<String, Object>>(parts, requestHeaders),
	  Void.class, "Cannot update MimeMultipart object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new HttpResponseStatusAndHeaders(response.getStatusCodeValue(),
	  null, response.getHeaders());
    } catch (LockssRestException lre) {
      log.debug2("Exception caught updating MimeMultipart object", lre);
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
    log.debug2("httpMethod = {}", httpMethod);
    log.debug2("body = {}", body);
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    // Initialize the request to the REST service.
    RestTemplate restTemplate = createRestTemplate(connectTimeout, readTimeout);

    log.trace("requestHeaders = {}", requestHeaders.toSingleValueMap());
    log.trace("Making {} request to '{}'...", httpMethod, uri);

    try {
      // Make the request to the REST service and get its response.
      ResponseEntity<MimeMultipart> response =
	  RestUtil.callRestService(restTemplate, uri, httpMethod,
	      new HttpEntity<>(body, requestHeaders), MimeMultipart.class,
	      "Cannot get MimeMultipart object");
      log.trace("response = {}", response);

      // Parse the response and return it.
      return new MultipartResponse(response);
    } catch (LockssRestException lre) {
      log.debug2("Exception caught getting MimeMultipart object", lre);
      log.debug2("uri = {}", uri);
      log.debug2("requestHeaders = {}", requestHeaders.toSingleValueMap());
      return new MultipartResponse(lre);
    } catch (IOException | MessagingException e) {
      log.error("Exception caught getting MimeMultipart object", e);
      log.error("uri = {}", uri);
      log.error("requestHeaders = {}", requestHeaders.toSingleValueMap());
      throw e;
    }
  }
}
