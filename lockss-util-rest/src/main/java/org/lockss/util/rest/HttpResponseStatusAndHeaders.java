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

import java.net.SocketTimeoutException;
import org.lockss.util.rest.exception.LockssRestException;
import org.lockss.util.rest.exception.LockssRestHttpException;
import org.lockss.util.rest.exception.LockssRestNetworkException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Encapsulation of an HTTP status code, message and response headers.
 */
public class HttpResponseStatusAndHeaders {
  private int code;
  private String message;
  private HttpHeaders headers;

  /**
   * Constructor from fields.
   *
   * @param code An int with the response status code.
   * @param message A String with the response status message.
   * @param headers An HttpHeaders with the response headers.
   */
  public HttpResponseStatusAndHeaders(int code, String message,
      HttpHeaders headers) {
    super();
    this.code = code;
    this.message = message;
    this.headers = headers;
  }

  public int getCode() {
    return code;
  }
  public HttpResponseStatusAndHeaders setCode(int code) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return message;
  }
  public HttpResponseStatusAndHeaders setMessage(String message) {
    this.message = message;
    return this;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }
  public HttpResponseStatusAndHeaders setMessage(HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  /**
   * Map a LockssRestException into an object of this class.
   * 
   * @param lre
   *          A LockssRestException with the exception to be mapped.
   * @return an HttpStatusCodeAndMessage with the mapping result.
   */
  public static HttpResponseStatusAndHeaders fromLockssRestException(
      LockssRestException lre) {
    if (lre == null) {
      return null;
    }

    // Check whether it is a network exception.
    if (lre instanceof LockssRestNetworkException) {
      // Yes: Check whether it is a timeout exception.
      if (lre.getCause() instanceof SocketTimeoutException) {
	// Yes.
	return new HttpResponseStatusAndHeaders(
	    HttpStatus.GATEWAY_TIMEOUT.value(), lre.getMessage(), null);
      }

      // No.
      return new HttpResponseStatusAndHeaders(HttpStatus.BAD_GATEWAY.value(),
	  lre.getMessage(), null);
      // Check whether it is an HTTP exception.
    } else if (lre instanceof LockssRestHttpException) {
      // Yes.
      LockssRestHttpException lrhe =(LockssRestHttpException)lre;

      return new HttpResponseStatusAndHeaders(lrhe.getHttpStatusCode(),
	  lrhe.getHttpStatusMessage(), lrhe.getHttpResponseHeaders());
    } else {
      // No: It is an unknown exception type.
      return new HttpResponseStatusAndHeaders(
	  HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unknown exception type",
	  null);
    }
  }

  @Override
  public String toString() {
    return "[HttpStatusCodeAndMessage code=" + code + ", message=" + message
	+ ", headers=" + headers + "]";
  }
}
