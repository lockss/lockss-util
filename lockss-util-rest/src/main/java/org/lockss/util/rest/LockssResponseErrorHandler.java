package org.lockss.util.rest;

import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;

public class LockssResponseErrorHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    HttpStatus statusCode = getHttpStatusCode(response);

    LockssRestHttpException lrhe = new LockssRestHttpException();

    lrhe.setHttpStatus(statusCode);
    lrhe.setHttpResponseHeaders(response.getHeaders());
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

    public LockssRestHttpException getLHRE() {
      return wrappedLRHE;
    }
  }
}
