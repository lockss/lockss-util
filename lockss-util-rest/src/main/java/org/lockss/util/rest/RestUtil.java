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
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;
import org.lockss.log.L4JLogger;
import org.lockss.util.Constants;
import org.lockss.util.io.DeferredTempFileOutputStream;
import org.lockss.util.lang.ExceptionUtil;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.exception.LockssRestNetworkException;
import org.lockss.util.rest.multipart.MultipartMessageHttpMessageConverter;
import org.lockss.util.time.TimerUtil;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods used for invoking REST services.
 */
public class RestUtil {
  private static L4JLogger log = L4JLogger.getLogger();

  public static final long[] DEFAULT_RETRY_BACKOFFS =
    new long[] {1000L /*, 10000L*/};
  public static final long[] NO_RETRY_BACKOFFS = new long[] {};

  public static final int DEFAULT_MAX_CONNECTIONS = 25;
  public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

  /**
   * Default timeout, in milliseconds, for establishing a connection to a REST
   * service.  Zero means no timeout.
   */
  public static final long DEFAULT_CONNECT_TIMEOUT = 30 * Constants.SECOND;

  /**
   * Default socket timeout, in milliseconds, for REST service calls.  This is
   * an inactivity timeout: it limits how long a single read from the socket may
   * block, not how long the call as a whole may take, so it does not limit the
   * time spent transferring a large response.  Zero means no timeout.
   */
  public static final long DEFAULT_READ_TIMEOUT = 2 * Constants.MINUTE;

  /**
   * Default socket timeout, in milliseconds, for REST service calls that are
   * expected to produce no response for a long time.  The archetype is
   * createArtifact, which doesn't respond until the artifact has been
   * compressed and stored.  Zero means no timeout.
   *
   * @see #callLongRunningRestService
   */
  public static final long DEFAULT_LONG_READ_TIMEOUT = 1 * Constants.HOUR;

  /**
   * Set while a call that {@link #callLongRunningRestService} is making is in
   * progress, to tell {@link LockssHttpRequestFactory} to apply the long read
   * timeout to it.
   */
  private static final ThreadLocal<Boolean> isLongRunningCall =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

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
   * Performs a call to a REST service that may legitimately produce no response
   * for a long time, using the long read timeout of the {@link RestTemplate}
   * instead of its normal read timeout.
   * <p>
   * Most REST calls have no excuse for taking a long time to <i>start</i>
   * responding, and are better off failing than blocking a thread
   * indefinitely.  A few do: createArtifact, for instance, doesn't respond
   * until the artifact has been compressed and stored, which can take
   * arbitrarily long.  Those calls should be made with this method.
   * <p>
   * The longer timeout applies only if the {@link RestTemplate} was built by
   * {@link #getRestTemplateBuilder(LockssRestTemplateSettings)} or one of the
   * methods that use it; with any other {@link RestTemplate} this behaves
   * exactly like {@link #callRestService}.
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
  public static <T> ResponseEntity<T> callLongRunningRestService(
      RestTemplate restTemplate,
      URI uri, HttpMethod method, HttpEntity<?> requestEntity,
      Class<T> responseType, String clientExceptionMessage)
	  throws LockssRestException {
    isLongRunningCall.set(Boolean.TRUE);

    try {
      return callRestService(restTemplate, uri, method, requestEntity,
                             responseType, clientExceptionMessage);
    } finally {
      isLongRunningCall.remove();
    }
  }

  /**
   * Provides an indication of whether the call in progress on this thread was
   * made by {@link #callLongRunningRestService}.
   *
   * @return a boolean with <code>true</code> if the long read timeout should be
   *         applied to the request being created.
   */
  static boolean isLongRunningCall() {
    return isLongRunningCall.get();
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
   * using the HttpComponentsClientHttpRequestFactory, {@link
   * #DEFAULT_CONNECT_TIMEOUT} and {@link #DEFAULT_READ_TIMEOUT}, and not
   * throwing exceptions.
   *
   * @return a RestTemplate with the REST template.
   */
  public static RestTemplate getRestTemplate() {
    return getRestTemplate(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
  }

  public static RestTemplateBuilder getRestTemplateBuilder(long connectTimeout, long readTimeout) {
    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);

    if (connectTimeout > 0 && connectTimeout < 1000) {
      log.warn("connectTimeout < 1 sec: {}", connectTimeout);
    }

    if (readTimeout > 0 && readTimeout < 1000) {
      log.warn("readTimeout < 1 sec: {}", readTimeout);
    }

    LockssRestTemplateSettings settings =
        LockssRestTemplateSettings.withTimeouts(connectTimeout, readTimeout);

    return getRestTemplateBuilder(settings);
  }

  public static RestTemplateBuilder getRestTemplateBuilder(LockssRestTemplateSettings settings) {
    // Q: Do we need to configure message converters?
    return new RestTemplateBuilder()
        .requestFactory(() -> createConfiguredRequestFactory(settings))
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
  public static RestTemplate getRestTemplate(long connectTimeout, long readTimeout) {
    // Q: Do we need to configure message converters? Error handlers?
    RestTemplate restTemplate = getRestTemplateBuilder(connectTimeout, readTimeout).build();
    log.debug2("restTemplate = {}", restTemplate);
    return restTemplate;
  }

  private static HttpComponentsClientHttpRequestFactory createConfiguredRequestFactory(LockssRestTemplateSettings settings) {
    HttpComponentsClientHttpRequestFactory requestFactory = createRequestFactory(settings);
    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
    map.from(Duration.ofMillis(settings.connectTimeout()))
        .asInt(Duration::toMillis).to(requestFactory::setConnectTimeout);
    return requestFactory;
  }

  private static HttpComponentsClientHttpRequestFactory createRequestFactory(LockssRestTemplateSettings settings) {
    return new LockssHttpRequestFactory(createHttpClient(settings),
                                        settings.readTimeout(),
                                        settings.longReadTimeout());
  }

  /**
   * Provides the socket timeout that a {@link RestTemplate} built by this class
   * applies to normal calls.  Intended for tests and diagnostics: the timeouts
   * are otherwise not retrievable from a {@link RestTemplate}.
   *
   * @param restTemplate A RestTemplate.
   * @return a long with the read timeout in milliseconds, zero if there is no
   *         timeout, or -1 if the RestTemplate was not built by this class.
   */
  public static long getReadTimeout(RestTemplate restTemplate) {
    return restTemplate.getRequestFactory() instanceof LockssHttpRequestFactory factory ?
        factory.getReadTimeout() : -1;
  }

  /**
   * Provides the socket timeout that a {@link RestTemplate} built by this class
   * applies to calls made by {@link #callLongRunningRestService}.  Intended for
   * tests and diagnostics.
   *
   * @param restTemplate A RestTemplate.
   * @return a long with the long read timeout in milliseconds, zero if there is
   *         no timeout, or -1 if the RestTemplate was not built by this class.
   */
  public static long getLongReadTimeout(RestTemplate restTemplate) {
    return restTemplate.getRequestFactory() instanceof LockssHttpRequestFactory factory ?
        factory.getLongReadTimeout() : -1;
  }

  /**
   * Provides the read timeout to apply to a long-running call.  A long-running
   * call is never given less time than a normal one, and a normal read timeout
   * of zero (no timeout) implies no timeout for long-running calls either.
   *
   * @param readTimeout     A long with the normal read timeout in milliseconds.
   * @param longReadTimeout A long with the requested long read timeout in
   *                        milliseconds.
   * @return a long with the read timeout to apply, in milliseconds.
   */
  static long effectiveLongReadTimeout(long readTimeout, long longReadTimeout) {
    if (readTimeout <= 0 || longReadTimeout <= 0) {
      return 0;
    }

    return Math.max(readTimeout, longReadTimeout);
  }

  /**
   * A request factory that applies the read timeout appropriate to the call
   * being made: the long read timeout if the call was made by {@link
   * #callLongRunningRestService}, the normal read timeout otherwise.
   * <p>
   * The socket timeout configured on the connection manager can't do this,
   * because it is a property of the pooled connection, not of the request.  A
   * response timeout in the request's {@link RequestConfig} overrides it for
   * the duration of the exchange, and is set explicitly on every request so
   * that no request inherits the timeout of the one that used the connection
   * before it.
   */
  static class LockssHttpRequestFactory
      extends HttpComponentsClientHttpRequestFactory {

    private final long readTimeout;
    private final long longReadTimeout;

    LockssHttpRequestFactory(HttpClient httpClient, long readTimeout,
                             long longReadTimeout) {
      super(httpClient);
      this.readTimeout = readTimeout;
      this.longReadTimeout =
          effectiveLongReadTimeout(readTimeout, longReadTimeout);
    }

    long getReadTimeout() {
      return readTimeout;
    }

    long getLongReadTimeout() {
      return longReadTimeout;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
      long timeout = isLongRunningCall() ? longReadTimeout : readTimeout;

      log.debug2("Read timeout for {} {}: {}", httpMethod, uri, timeout);

      // Start from the config Spring would otherwise have used, so that
      // everything else it configures (notably the connection request timeout)
      // is preserved
      RequestConfig requestConfig = createRequestConfig(getHttpClient());

      RequestConfig.Builder builder = requestConfig == null ?
          RequestConfig.custom() : RequestConfig.copy(requestConfig);

      // Timeout.ofMilliseconds(0) means no timeout, which is the convention
      // used for the timeouts throughout this class
      builder.setResponseTimeout(Timeout.ofMilliseconds(Math.max(timeout, 0)));

      HttpClientContext context = HttpClientContext.create();
      context.setRequestConfig(builder.build());
      return context;
    }
  }

  private static HttpClient createHttpClient(LockssRestTemplateSettings settings) {
    PoolingHttpClientConnectionManager connectionManager = createPoolingConnectionManager(settings);
    return HttpClientBuilder.create().useSystemProperties().setConnectionManager(connectionManager)
        .build();
  }

  private static PoolingHttpClientConnectionManager createPoolingConnectionManager(LockssRestTemplateSettings settings) {
    PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder =
        PoolingHttpClientConnectionManagerBuilder.create();

    connectionManagerBuilder.setMaxConnTotal(settings.maxConnections());
    connectionManagerBuilder.setMaxConnPerRoute(settings.maxConnectionsPerRoute());

    SocketConfig socketConfig = SocketConfig.custom()
        .setSoTimeout((int) settings.readTimeout(), TimeUnit.MILLISECONDS)
        .build();
    connectionManagerBuilder.setDefaultSocketConfig(socketConfig);

    ConnectionConfig connectionConfig = ConnectionConfig.custom()
        .setConnectTimeout((int) settings.connectTimeout(), TimeUnit.MILLISECONDS)
        .setSocketTimeout((int) settings.readTimeout(), TimeUnit.MILLISECONDS)
        .build();
    connectionManagerBuilder.setDefaultConnectionConfig(connectionConfig);

    if (settings.sslBundle() != null) {
      SslOptions options = settings.sslBundle().getOptions();
      SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(settings.sslBundle().createSslContext(),
          options.getEnabledProtocols(), options.getCiphers(), new DefaultHostnameVerifier());
      connectionManagerBuilder.setSSLSocketFactory(socketFactory);
    }

    PoolingHttpClientConnectionManager connectionManager =
        connectionManagerBuilder.useSystemProperties().build();

    return connectionManager;
  }

  public static class LockssRestTemplateSettingsBuilder {
    long connectTimeout;
    long readTimeout;
    long longReadTimeout = DEFAULT_LONG_READ_TIMEOUT;
    int maxConnections;
    int maxConnectionsPerRoute;
    long dfosSizeThreshold;
    File dfosTmpDir;
    SslBundle sslBundle;

    public LockssRestTemplateSettings build() {
      return new LockssRestTemplateSettings(connectTimeout, readTimeout, longReadTimeout, maxConnections,
          maxConnectionsPerRoute, dfosSizeThreshold, dfosTmpDir, sslBundle);
    }

    public LockssRestTemplateSettingsBuilder setConnectTimeout(long connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setReadTimeout(long readTimeout) {
      this.readTimeout = readTimeout;
      return this;
    }

    /** Set the read timeout used for calls made by {@link
     * RestUtil#callLongRunningRestService} */
    public LockssRestTemplateSettingsBuilder setLongReadTimeout(long longReadTimeout) {
      this.longReadTimeout = longReadTimeout;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setMaxConnections(int maxConnections) {
      this.maxConnections = maxConnections;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
      this.maxConnectionsPerRoute = maxConnectionsPerRoute;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setDfosSizeThreshold(long dfosSizeThreshold) {
      this.dfosSizeThreshold = dfosSizeThreshold;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setDfosTmpDir(File dfosTmpDir) {
      this.dfosTmpDir = dfosTmpDir;
      return this;
    }

    public LockssRestTemplateSettingsBuilder setSslBundle(SslBundle sslBundle) {
      this.sslBundle = sslBundle;
      return  this;
    }
  }

  public record LockssRestTemplateSettings(
    long connectTimeout,
    long readTimeout,
    long longReadTimeout,
    int maxConnections,
    int maxConnectionsPerRoute,
    long dfosSizeThreshold,
    File dfosTmpDir,
    SslBundle sslBundle) {

    public static LockssRestTemplateSettings withTimeouts(long connectTimeout, long readTimeout) {
      return new LockssRestTemplateSettingsBuilder()
          .setConnectTimeout(connectTimeout)
          .setReadTimeout(readTimeout)
          .setMaxConnections(DEFAULT_MAX_CONNECTIONS)
          .setMaxConnectionsPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
          .build();
    }

    /** Provides a copy of these settings with the given read timeout for
     * calls made by {@link RestUtil#callLongRunningRestService} */
    public LockssRestTemplateSettings withLongReadTimeout(long longReadTimeout) {
      return new LockssRestTemplateSettings(connectTimeout, readTimeout,
          longReadTimeout, maxConnections, maxConnectionsPerRoute,
          dfosSizeThreshold, dfosTmpDir, sslBundle);
    }
  }

  public static RestTemplate getRestTemplate(long connectTimeout,
                                             long readTimeout,
                                             int sizeThreshold,
                                             File tmpDir) {

    log.debug2("connectTimeout = {}", connectTimeout);
    log.debug2("readTimeout = {}", readTimeout);
    log.debug2("sizeThreshold = {}", sizeThreshold);
    log.debug2("tmpDir = {}", tmpDir);

    LockssRestTemplateSettings settings =
        LockssRestTemplateSettings.withTimeouts(connectTimeout, readTimeout);

    return getRestTemplate(settings, replaceHttpMessageConverter(new RestTemplate().getMessageConverters(),
        ResourceHttpMessageConverter.class, getResourceHttpMessageConverter(sizeThreshold, tmpDir)));
  }

  public static RestTemplate getRestTemplate(LockssRestTemplateSettings settings) {
    return getRestTemplate(settings, replaceHttpMessageConverter(
        new RestTemplate().getMessageConverters(),
        ResourceHttpMessageConverter.class,
        getResourceHttpMessageConverter((int) settings.dfosSizeThreshold(), settings.dfosTmpDir())));
  }

  public static RestTemplate getRestTemplate(LockssRestTemplateSettings settings,
                                             List<HttpMessageConverter<?>> msgConverters) {
    return getRestTemplateBuilder(settings)
        .messageConverters(msgConverters)
        .errorHandler(new LockssResponseErrorHandler(new RestTemplate().getMessageConverters()))
        .build();
  }

  private static List<HttpMessageConverter<?>> replaceHttpMessageConverter(List<HttpMessageConverter<?>> msgConverters,
                                                                           Class<? extends HttpMessageConverter> msgConverterClass,
                                                                           HttpMessageConverter<?> msgConverter) {
    return msgConverters.stream()
        .map(converter -> converter.getClass() == msgConverterClass ? msgConverter : converter)
        .toList();
  }

  /**
   * This method creates a {@link ResourceHttpMessageConverter} that manages large resource data
   * by leveraging a deferred file output stream in cases where data exceeds a specified size threshold.
   *
   * @param sizeThreshold the size threshold (in bytes) above which the resource data will be written to a temporary file
   * @param tmpDir the directory to use for storing temporary files when the size threshold is exceeded
   * @return a configured {@link ResourceHttpMessageConverter} instance for handling {@link Resource} instances
   */
  private static HttpMessageConverter<?> getResourceHttpMessageConverter(int sizeThreshold, File tmpDir) {
    return new ResourceHttpMessageConverter(true) {
      @Override
      protected Resource readInternal(Class<? extends Resource> clazz, HttpInputMessage inputMessage)
          throws IOException, HttpMessageNotReadableException {

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

    Map<String,String> valMap = new HashMap<>();
    if (uriVariables != null) {
      valMap.putAll(uriVariables);
    }

    // Add any query parameters.
    if (queryParams != null) {
      valMap.putAll(queryParams);
      for (String key : queryParams.keySet()) {
        builder.queryParam(key, "{" + key + "}");
      }
    }
    log.trace("valMap = {}", valMap);

    // Interpolate any URI and query variables if present and build
    URI uri = builder
      .encode()
      .build()
      .expand(valMap)
      .toUri();
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
