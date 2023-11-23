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
