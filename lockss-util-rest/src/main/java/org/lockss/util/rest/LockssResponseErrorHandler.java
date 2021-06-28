package org.lockss.util.rest;

import org.lockss.log.L4JLogger;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * LOCKSS customized {@link ResponseErrorHandler} for use with {@link RestTemplate} REST clients.
 */
public class LockssResponseErrorHandler extends DefaultResponseErrorHandler {
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * {@link List} of {@link HttpMessageConverter} used to deserialize error responses from Spring Boot applications.
   */
  private final List<HttpMessageConverter<?>> messageConverters;

  /**
   * Constructor.
   *
   * @param messageConverters A {@link List<HttpMessageConverter>} containing the list of HTTP message converters
   *                          this {@link LockssResponseErrorHandler} should use to deserialize error responses.
   */
  public LockssResponseErrorHandler(List<HttpMessageConverter<?>> messageConverters) {
    this.messageConverters = messageConverters;
  }

  /**
   * Overrides {@link DefaultResponseErrorHandler#handleError(ClientHttpResponse)} to intercept 5xx server errors
   * and determine the type of error the LOCKSS Spring Boot application is experiencing.
   * <p>
   * Use a provided list of {@link HttpMessageConverter}s to deserialize the error responses from LOCKSS Spring Boot
   * applications into {@link RestResponseErrorBody.RestResponseError} objects, which are then used to populate the
   * {@link LockssRestHttpException} that's wrapped and thrown within a {@link WrappedLockssRestHttpException} to
   * the client call.
   *
   * @param response A {@link ClientHttpResponse} representing the HTTP error response from the Spring Boot application.
   * @throws IOException
   */
  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    try {
      // We could process the ClientHttpResponse directly but the RestClientResponseException thrown
      // by the super class method has a convenient access to the error response body byte array, which
      // in-turn is useful for avoiding successive HttpMessageConverters from using an exhausted InputStream
      // from the ClientHttpResponse. See below and ByteArrayHttpInputMessage.
      super.handleError(response);

    } catch (RestClientResponseException e1) {
      //// Intercept RestClientResponseException and translate to LockssRestHttpException

      // Wrap and throw the LockssRestHttpException in an unchecked WrappedLockssRestHttpException
      throw new WrappedLockssRestHttpException(
          LockssRestHttpException.fromRestClientResponseException(e1, messageConverters)
      );
    }
  }

  /**
   * This is needed because {@link RestTemplate#doExecute(URI, HttpMethod, RequestCallback, ResponseExtractor)} catches
   * {@link IOException}, which is extended by {@link LockssRestException} and its subclass
   * {@link LockssRestHttpException}.
   */
  public static class WrappedLockssRestHttpException extends RuntimeException {
    private LockssRestHttpException wrappedLRHE;

    public WrappedLockssRestHttpException(LockssRestHttpException e) {
      this.wrappedLRHE = e;
    }

    public LockssRestHttpException getLRHE() {
      return wrappedLRHE;
    }
  }
}
