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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lockss.log.L4JLogger;
import org.lockss.util.io.DeferredTempFileOutputStream;
import org.lockss.util.lang.ExceptionUtil;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.exception.LockssRestNetworkException;
import org.lockss.util.rest.multipart.MultipartMessageHttpMessageConverter;
import org.lockss.util.rest.repo.RestLockssRepository;
import org.lockss.util.rest.repo.model.Artifact;
import org.lockss.util.time.TimerUtil;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Utility methods used for invoking REST services.
 */
public class RestUtil {
  private static L4JLogger log = L4JLogger.getLogger();

  public static final long[] DEFAULT_RETRY_BACKOFFS =
    new long[] {1000L /*, 10000L*/};
  public static final long[] NO_RETRY_BACKOFFS = new long[] {};

  /**
   * Performs a call to a REST service with the default retry backoffs
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
   * @param clientExceptionMessage
   *          A String with the message to be returned if there are errors.
   * @return a {@code ResponseEntity<T>} with the response to the request to the
   *         REST service.
   * @throws LockssRestException
   *           if there are problems making the request to the REST service.
   */
  public static <T> ResponseEntity<T> callRestService(RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String clientExceptionMessage)
	  throws LockssRestException {
    return callRestService(restTemplate, uri, method, requestEntity,
                           responseType, clientExceptionMessage,
                           null);
  }

  /**
   * Performs a call to a REST service, once with no retries.
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
   * @param clientExceptionMessage
   *          A String with the message to be returned if there are errors.
   * @return a {@code ResponseEntity<T>} with the response to the request to the
   *         REST service.
   * @throws LockssRestException
   *           if there are problems making the request to the REST service.
   */
  public static <T> ResponseEntity<T> callRestServiceNoRetry(
      RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String clientExceptionMessage)
	  throws LockssRestException {
    return callRestService(restTemplate, uri, method, requestEntity,
                           responseType, clientExceptionMessage,
                           NO_RETRY_BACKOFFS);
  }

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
   * @param clientExceptionMessage
   *          A String with the message to be returned if there are errors.
   * @param retryBackoffs
   *          An array of longs specifying successive intervals
   *          to wait between successive retries.  If empty, no retries.
   * @return a {@code ResponseEntity<T>} with the response to the request to the
   *         REST service.
   * @throws LockssRestException
   *           if there are problems making the request to the REST service.
   */
  public static <T> ResponseEntity<T> callRestService(RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String clientExceptionMessage,
      long[] retryBackoffs)
          throws LockssRestException {

    log.debug2("uri = {}", uri);
    log.debug2("method = {}", method);
    log.debug2("requestEntity = {}", requestEntity);
    log.debug2("responseType = {}", responseType);
    log.debug2("clientExceptionMessage = {}", clientExceptionMessage);

    if (retryBackoffs == null) {
      retryBackoffs = DEFAULT_RETRY_BACKOFFS;
    }
    int retryIx = 0;
    while (true) {
      try {
        return RestUtil.callRestServiceOnce(restTemplate, uri, method,
                                            requestEntity, responseType,
                                            clientExceptionMessage);
      } catch (LockssRestNetworkException lrne) {
        if (isRetryableException(lrne) &&
            retryIx < retryBackoffs.length) {
          long backoff = retryBackoffs[retryIx++];
          log.debug("Retrying {} {} after waiting {}, due to {}",
                    method, uri, backoff, lrne.toString());
          try {
            TimerUtil.sleep(backoff);
          } catch (InterruptedException ie) {
            // If interrupted, throw the last failure exception
            throw lrne;
          }
        } else {
          throw lrne;
        }
      }
    }
  }

  static boolean isRetryableException(Exception e) {
    Throwable quickFailException =
      ExceptionUtil.getNestedExceptionOfType(e,
                                             ConnectException.class,
                                             UnknownHostException.class
                                             );
    if (quickFailException != null) {
      return true;
    }
    return false;
  }



  public static <T> ResponseEntity<T> callRestServiceOnce(RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String clientExceptionMessage)
	  throws LockssRestException {

    try {
      // Make the call to the REST service and get the response.
      ResponseEntity<T> response = restTemplate.exchange(uri, method, requestEntity, responseType);

      // Get the response status.
      HttpStatusCode statusCode = response.getStatusCode();
      HttpStatus status = HttpStatus.valueOf(statusCode.value());

      log.trace("status = {}", status);

      // Check whether the call status code indicated failure.
      // Q: It's possible that this is never taken because 1xx and 3xx series of errors also cause
      //  RestTemplate#exchange(...) to throw
      if (!isSuccess(statusCode)) {
        // Yes: Report it back to the caller.
        LockssRestHttpException lrhe = new LockssRestHttpException(clientExceptionMessage);
        lrhe.setHttpStatus(status);
        lrhe.setHttpResponseHeaders(response.getHeaders());

        log.trace("lrhe = {}", lrhe, (Exception) null);

        throw lrhe;
      }

      // No: Return the received response.
      return response;

    } catch (LockssResponseErrorHandler.WrappedLockssRestHttpException e) {
      // Thrown by LockssResponseErrorHandler. This workaround is needed because RestTemplate#doExecute(...) catches
      // IOException which is subclassed by LockssRestHttpException.

      LockssRestHttpException lrhe = e.getLRHE();
      lrhe.setClientErrorMessage(clientExceptionMessage);
      throw lrhe;

    } catch (RestClientResponseException e) {
      // Since this method takes a RestTemplate as an argument, we need to be prepared for the possibility that it
      // was not configured with our LockssResponseErrorHandler. Handle default RestClientResponseException and its
      // subclasses here.

      throw LockssRestHttpException.fromRestClientResponseException(e, restTemplate.getMessageConverters());

    } catch (ResourceAccessException e) {
      // Get the cause, or this exception if there is no cause.
      Throwable cause = e.getCause();

      if (cause == null) {
        cause = e;
      }

      // Report the problem back to the caller.
      LockssRestNetworkException lrne =
          new LockssRestNetworkException("Client exception: " +
              clientExceptionMessage + ": " + ExceptionUtils.getRootCauseMessage(cause), cause);

      log.trace("lrne = {}", lrne);

      throw lrne;
    }
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

  public static RestTemplateBuilder getRestTemplateBuilder(long connectTimeout,
                                                           long readTimeout) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    if (connectTimeout > 0 && connectTimeout < 1000) {
      log.warn("connectTimeout < 1 sec: {}", connectTimeout);
    }

    if (readTimeout > 0 && readTimeout < 1000) {
      log.warn("readTimeout < 1 sec: {}", readTimeout);
    }

    return new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(connectTimeout))
        .setReadTimeout(Duration.ofMillis(readTimeout))
        .errorHandler(new LockssResponseErrorHandler(new RestTemplate().getMessageConverters()));
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

    if (connectTimeout > 0 && connectTimeout < 1000) {
      log.warn("connectTimeout < 1 sec: {}", connectTimeout);
    }
    if (readTimeout > 0 && readTimeout < 1000) {
      log.warn("readTimeout < 1 sec: {}", readTimeout);
    }

    RestTemplateBuilder builder = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(connectTimeout))
        .setReadTimeout(Duration.ofMillis(readTimeout))
        .errorHandler(new LockssResponseErrorHandler(new RestTemplate().getMessageConverters()));

    RestTemplate restTemplate =	builder.build();

    log.debug2("restTemplate = {}", restTemplate);
    return restTemplate;
  }

  public static RestTemplate getRestTemplate(long connectTimeout,
                                             long readTimeout,
                                             int sizeThreshold,
                                             File tmpDir) {

    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);
    log.debug2("sizeThreshold = {}", sizeThreshold);
    log.debug2("tmpDir = {}", tmpDir);

    RestTemplateBuilder builder = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(connectTimeout))
        .setReadTimeout(Duration.ofMillis(readTimeout))
        .errorHandler(new LockssResponseErrorHandler(new RestTemplate().getMessageConverters()));

//    List<HttpMessageConverter<?>> msgConverters = new RestTemplate().getMessageConverters()
//        .stream()
//        .map(msgConv -> msgConv instanceof ResourceHttpMessageConverter ?
//            getResourceHttpMessageConverter(sizeThreshold, tmpDir) : msgConv)
//        .toList();
//
//    builder.messageConverters(msgConverters);

    return builder.build();
  }

  /**
   * Returns a patched version of {@link ResourceHttpMessageConverter} that fixes {@link InputStreamResource}
   * requests by ensuring the {@link InputStream} its {@code getInputStream()} returns is not closed by
   * {@link RestTemplate} before reaching client code. It does this by buffering a copy of the stream in a DTFOS.
   *
   * No longer used since {@link RestLockssRepository#getArtifactData(Artifact)} has been modified to make HTTP
   * requests directly to the Repository service. Kept for posterity.
   */
  private static HttpMessageConverter<?> getResourceHttpMessageConverter(int sizeThreshold, File tmpDir) {
    return new ResourceHttpMessageConverter(true) {
      @Override
      protected Resource readInternal(Class<? extends Resource> clazz, HttpInputMessage inputMessage)
          throws IOException, HttpMessageNotReadableException {

        // We call super.readInterval() with a custom HttpInputMessage that creates a DTFOS from the
        // input stream for InputStreamResource, or passes along the input stream for all other cases:
        return super.readInternal(clazz, new HttpInputMessage() {
          @Override
          public InputStream getBody() throws IOException {
            if (InputStreamResource.class == clazz) {
              DeferredTempFileOutputStream dtfos =
                  new DeferredTempFileOutputStream(sizeThreshold, tmpDir);

              IOUtils.copy(inputMessage.getBody(), dtfos);
              dtfos.close();

              return dtfos.getDeleteOnCloseInputStream();
            } else {
              return inputMessage.getBody();
            }
          }

          @Override
          public HttpHeaders getHeaders() {
            return inputMessage.getHeaders();
          }
        });
      }
    };
  }

  /**
   * Add a MultipartMessageHttpMessageConverter to the RestTemplate.
   * Temp files will be stored in the System temp dir
   * @param restTemplate The RestTemplate to which to add the converter
   */
  public static RestTemplate addMultipartConverter(RestTemplate restTemplate) {
    return addMultipartConverter(restTemplate, null);
  }

  /**
   * Add a MultipartMessageHttpMessageConverter to the RestTemplate.
   * Temp files will be stored in the specified dir, else  the System
   * temp dir if null
   * @param restTemplate The RestTemplate to which to add the converter
   * @param tmpDir The directory into which to store temp files.  If
   * null the System temp dir will be used
   */
  public static RestTemplate addMultipartConverter(RestTemplate restTemplate,
                                                   File tmpDir) {
    // Get the current message converters.
    List<HttpMessageConverter<?>> messageConverters =
      restTemplate.getMessageConverters();
    log.trace("messageConverters = {}", messageConverters);

    // Add the multipart/form-data converter.
    messageConverters.add(new MultipartMessageHttpMessageConverter(tmpDir));
    log.trace("messageConverters = {}", messageConverters);
    return restTemplate;
  }

  /**
   * Add a FormCnoverter to the RestTemplate.
   * @param restTemplate The RestTemplate to which to add the converter
   */
  public static RestTemplate addFormConverter(RestTemplate restTemplate) {
    // Get the current message converters.
    List<HttpMessageConverter<?>> messageConverters =
      restTemplate.getMessageConverters();
    log.trace("messageConverters = {}", messageConverters);

    // Add the form-data converter.
    messageConverters.add(new FormHttpMessageConverter());
    log.trace("messageConverters = {}", messageConverters);
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
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uriString);
    log.trace("builder = {}", builder);

    // Add any query parameters.
    if (queryParams != null && !queryParams.isEmpty()) {
      for (String key : queryParams.keySet()) {
        String value = queryParams.get(key);
        log.trace("key = {}, value = {}", key, value);
        builder.queryParam(key, value);
      }
    }

    // Interpolate any URI variables if present and build
    UriComponents uriComponents =
        (uriVariables != null && !uriVariables.isEmpty()) ?
            builder.buildAndExpand(uriVariables) : builder.build();

    URI uri = uriComponents.encode().toUri();
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
  public static boolean isSuccess(HttpStatusCode statusCode) {
    return statusCode.is2xxSuccessful();
  }
}
