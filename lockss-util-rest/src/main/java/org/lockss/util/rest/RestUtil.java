/*

 Copyright (c) 2019-2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest;

import java.net.URI;
import java.util.Map;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.exception.LockssRestNetworkException;
import org.lockss.log.L4JLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility methods used for invoking REST services.
 */
public class RestUtil {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * Performs a call to a REST service.
   * 
   * @param restTemplate
   *          A RestTemplate with the REST template to be used to access the
   *          REST service.
   * @param uri
   *          A String with the URI of the request to the REST service.
   * @param method
   *          An HttpMethod with the method of the request to the REST service.
   * @param requestEntity
   *          An HttpEntity with the entity of the request to the REST service.
   * @param responseType
   *          A {@code Class<T>} with the expected type of the response to the
   *          request to the REST service.
   * @param exceptionMessage
   *          A String with the message to be returned if there are errors.
   * @return a {@code ResponseEntity<T>} with the response to the request to the
   *         REST service.
   * @throws LockssRestException
   *           if there are problems making the request to the REST service.
   */
  public static <T> ResponseEntity<T> callRestService(RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String exceptionMessage)
	  throws LockssRestException {
    log.debug2("uri = {}", uri);
    log.debug2("method = {}", method);
    log.debug2("requestEntity = {}", requestEntity);
    log.debug2("responseType = {}", responseType);
    log.debug2("exceptionMessage = {}", exceptionMessage);

    try {
      // Make the call to the REST service and get the response.
      ResponseEntity<T> response =
	  restTemplate.exchange(uri, method, requestEntity, responseType);

      // Get the response status.
      HttpStatus statusCode = response.getStatusCode();
      log.trace("statusCode = {}", statusCode);

      // Check whether the call status code indicated failure.
      if (!isSuccess(statusCode)) {
	// Yes: Report it back to the caller.
	LockssRestHttpException lrhe =
	    new LockssRestHttpException(exceptionMessage);
	lrhe.setHttpStatus(statusCode);
	lrhe.setHttpResponseHeaders(response.getHeaders());
	log.trace("lrhe = {}", lrhe, (Exception)null);

	throw lrhe;
      }

      // No: Return the received response.
      return response;
    } catch (RestClientException rce) {
      log.trace("rce", rce);
      // Get the cause, or this exception if there is no cause.
      Throwable cause = rce.getCause();

      if (cause == null) {
	cause = rce;
      }

      // Report the problem back to the caller.
      LockssRestNetworkException lrne =
	new LockssRestNetworkException(exceptionMessage + ": " +
	    ExceptionUtils.getRootCauseMessage(cause), cause);
      log.trace("lrne = {}", lrne);

      throw lrne;
    }
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the SimpleClientHttpRequestFactory, default timeouts and not throwing
   * exceptions.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getSimpleFactoryRestTemplate() {
    return getSimpleFactoryRestTemplate(0, 0, false);
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the SimpleClientHttpRequestFactory and default timeouts.
   * 
   * @param doThrow A boolean indicating whether to throw exceptions instead of
   *                returning an error status code.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getSimpleFactoryRestTemplate(boolean doThrow) {
    return getSimpleFactoryRestTemplate(0, 0, doThrow);
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the SimpleClientHttpRequestFactory and not throwing exceptions.
   * 
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getSimpleFactoryRestTemplate(long connectTimeout,
      long readTimeout) {
    return getSimpleFactoryRestTemplate(connectTimeout, readTimeout, false);
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the SimpleClientHttpRequestFactory.
   * 
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   * @param doThrow        A boolean indicating whether to throw exceptions
   *                       instead of returning an error status code.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getSimpleFactoryRestTemplate(long connectTimeout,
      long readTimeout, boolean doThrow) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);
    log.debug2("doThrow = {}", doThrow);

    SimpleClientHttpRequestFactory requestFactory =
	new SimpleClientHttpRequestFactory();

    // Specify the timeouts.
    requestFactory.setConnectTimeout((int)connectTimeout);
    requestFactory.setReadTimeout((int)readTimeout);

    // Do not buffer the request body internally, to avoid running out of
    // memory, or other failures, when sending large amounts of data.
    //requestFactory.setBufferRequestBody(false);

    // Get the template.
    RestTemplate restTemplate =	new RestTemplate(requestFactory);

    // Check whether do not throw exceptions instead of returning an error
    // status code.
    if (!doThrow) {
      // Yes: Do not throw exceptions on non-success response status codes.
      restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
	protected boolean hasError(HttpStatus statusCode) {
	  return false;
	}
      });
    }

    log.debug2("restTemplate = {}", restTemplate);
    return restTemplate;
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the HttpComponentsClientHttpRequestFactory, default timeouts and not
   * throwing exceptions.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getRestTemplate() {
    return getRestTemplate(0, 0);
  }

  /**
   * Provides the REST template to be used to make the call to a REST service
   * using the HttpComponentsClientHttpRequestFactory and not throwing
   * exceptions.
   * 
   * @param connectTimeout A long with the connection timeout in milliseconds.
   * @param readTimeout    A long with the read timeout in milliseconds.
   * 
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getRestTemplate(long connectTimeout,
      long readTimeout) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    // It's necessary to specify this factory to get Spring support for PATCH
    // operations.
    HttpComponentsClientHttpRequestFactory requestFactory =
	new HttpComponentsClientHttpRequestFactory();

    // Specify the timeouts.
    requestFactory.setConnectTimeout((int)connectTimeout);
    requestFactory.setReadTimeout((int)readTimeout);

    // Do not buffer the request body internally, to avoid running out of
    // memory, or other failures, when sending large amounts of data.
    requestFactory.setBufferRequestBody(false);

    // Get the template.
    RestTemplate restTemplate =	new RestTemplate(requestFactory);

    // Do not throw exceptions on non-success response status codes.
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      protected boolean hasError(HttpStatus statusCode) {
	return false;
      }
    });

    log.debug2("restTemplate = {}", restTemplate);
    return restTemplate;
  }

  /**
   * Provides the URI to be used to make the call to a REST service.
   * 
   * @param uriString    A String with the REST Service URI.
   * @param uriVariables A Map<String, String> with any URI variables to be
   *                     interpolated..
   * @param queryParams  A Map<String, String> with any query parameters.
   * 
   * @return a URI with the REST service URI.
   */
  public static URI getRestUri(String uriString,
      Map<String, String> uriVariables, Map<String, String> queryParams) {
    log.debug2("uriString = {}", uriString);
    log.debug2("uriVariables = {}", uriVariables);
    log.debug2("queryParams = {}", queryParams);

    // Initialize the URI.
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
	.build();
    log.trace("uriComponents = {}", uriComponents);

    // Interpolate any URI variables.
    if (uriVariables != null && !uriVariables.isEmpty()) {
      uriComponents = uriComponents.expand(uriVariables);
    }

    log.trace("uriComponents = {}", uriComponents);

    UriComponentsBuilder ucb =
	UriComponentsBuilder.newInstance().uriComponents(uriComponents);
  
    // Add any query parameters.
    if (queryParams != null && !queryParams.isEmpty()) {
      for (String key : queryParams.keySet()) {
	log.trace("key = {}", key);
	String value = queryParams.get(key);
	log.trace("value = {}", value);

	ucb = ucb.queryParam(key, value);
      }
    }

    URI uri = ucb.build().encode().toUri();
    log.debug2("uri = {}", uri);
    return uri;
  }

  /**
   * Provides an indication of whether a successful response has been obtained.
   * 
   * @param statusCode
   *          An HttpStatus with the response status code.
   * @return a boolean with <code>true</code> if a successful response has been
   *         obtained, <code>false</code> otherwise.
   */
  public static boolean isSuccess(HttpStatus statusCode) {
    return statusCode.is2xxSuccessful();
  }
}
