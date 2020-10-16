package org.lockss.util.rest;

import org.lockss.log.L4JLogger;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class LockssResponseErrorHandler extends DefaultResponseErrorHandler {
  private static L4JLogger log = L4JLogger.getLogger();

  private final List<HttpMessageConverter<?>> messageConverters;

  public LockssResponseErrorHandler(List<HttpMessageConverter<?>>
                                        messageConverters) {
    this.messageConverters = messageConverters;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    HttpStatus statusCode = getHttpStatusCode(response);
    HttpHeaders responseHeaders = response.getHeaders();

    LockssRestHttpException lrhe = new LockssRestHttpException();

    boolean handled = false;

    // Convert the response body to e Map if possible (presumably because
    // the response is json)
    for (HttpMessageConverter converter : messageConverters) {
      if (converter.canRead(Map.class, responseHeaders.getContentType())) {
        try {
          Map mmm = (Map) converter.read(Map.class, response);
          log.debug2("Server error map: {}", mmm);
          lrhe.setServerErrorMap(mmm);
          if (mmm.containsKey("message")) {
            Object msg = mmm.get("message");
            if (msg instanceof String) {
              lrhe.setServerErrorMessage((String) msg);
            }
          }
          handled = true;
          break;
        } catch (Exception e) {
          log.debug2("Failed to convert body of {} response to Map", statusCode, e);
        }
      }
    }

    if (!handled) {
      // Convert the response body to e String if possible (presumably because
      // the response is text/*)
      for (HttpMessageConverter converter : messageConverters) {
        if (converter.canRead(String.class, responseHeaders.getContentType())) {
          try {
            String sss = (String) converter.read(String.class, response);
            log.debug2("Server error message: {}", sss);
            lrhe.setServerErrorMessage(sss);
            handled = true;
            break;
          } catch (Exception e) {
            log.debug2("Failed to convert body of {} response to String", statusCode, e);
          }
        }
      }
    }

    if (!handled) {
      log.debug("Error body not handled: {}", responseHeaders.getContentType());
    }

    lrhe.setHttpStatus(statusCode);
    lrhe.setHttpResponseHeaders(responseHeaders);
    lrhe.setMessage("FIXME");

    throw new WrappedLockssRestHttpException(lrhe);
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
