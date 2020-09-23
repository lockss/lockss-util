package org.lockss.util.rest;

import java.util.*;
import org.lockss.log.*;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;

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
    lrhe.setHttpStatus(statusCode);

    boolean handled = false;
    // Convert the response body to e Map if possible (presumably because
    // the response is json)
    try {
      for (HttpMessageConverter converter : messageConverters) {
	if (converter.canRead(Map.class, responseHeaders.getContentType())) {
	  Map mmm = (Map)converter.read(Map.class, response);
	  log.debug2("Server error map: {}", mmm);
	  lrhe.setServerErrorMap(mmm);
	  if (mmm.containsKey("message")) {
	    Object msg = mmm.get("message");
	    if (msg instanceof String) {
	      lrhe.setServerErrorMessage((String)msg);
	    }
	  }
	  handled = true;
	}
      }
      if (!handled) {
	// Convert the response body to e String if possible (presumably because
	// the response is text/*)
	for (HttpMessageConverter converter : messageConverters) {
	  if (converter.canRead(String.class,
				responseHeaders.getContentType())) {
	    String sss = (String)converter.read(String.class, response);
	    log.debug2("Server error message: {}", sss);
	    lrhe.setServerErrorMessage(sss);
	    handled = true;
	  }
	}
      }
      if (!handled) {
	log.debug("Error body not handled: {}",
		  responseHeaders.getContentType());
      }
    } catch (Exception e) {
      log.warn("Failed to convert body of {} response", statusCode, e);
    }
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
