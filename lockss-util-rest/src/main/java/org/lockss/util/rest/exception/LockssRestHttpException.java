/*

 Copyright (c) 2019 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest.exception;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class LockssRestHttpException extends LockssRestException {
  private static final long serialVersionUID = -4151454747192096531L;

  private HttpStatus httpStatus;
  private String httpStatusMessage;
  private HttpHeaders responseHeaders;
  private String srvErrMessage;
  private Map srvErrMap;

  // Temporary
  private String contextMessage;

  /**
   * Default constructor.
   */
  public LockssRestHttpException() {
    super();
  }

  /**
   * Constructor with a specified message.
   * 
   * @param message
   *          A String with the exception message.
   */
  public LockssRestHttpException(String message) {
    super(message);
  }

  /**
   * Provides the HttpStatus object.
   *
   * @return the HttpStatus
   */
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  /**
   * Provides the HTTP status code.
   * 
   * @return an int with the HTTP status code.
   */
  public int getHttpStatusCode() {
    return httpStatus.value();
  }

  /**
   * Saves the HttpStatus.
   * 
   * @param stat the HttpStatus object
   * @return a LockssRestHttpException with this object.
   */
  public LockssRestHttpException setHttpStatus(HttpStatus stat) {
    this.httpStatus = stat;
    return this;
  }

  /**
   * Provides the HTTP status message.
   * 
   * @return a String with the HTTP status message.
   */
  public String getHttpStatusMessage() {
    return httpStatus.getReasonPhrase();
  }

  /**
   * Provides the HTTP response headers.
   * 
   * @return an HttpHeaders with the HTTP response headers.
   */
  public HttpHeaders getHttpResponseHeaders() {
    return responseHeaders;
  }

  /**
   * Saves the HTTP response headers.
   * 
   * @param httpStatusMessage
   *          An HttpHeaders with the HTTP response headers.
   * @return a LockssRestHttpException with this object.
   */
  public LockssRestHttpException setHttpResponseHeaders(
      HttpHeaders responseHeaders) {
    this.responseHeaders = responseHeaders;
    return this;
  }

  /**
   * Return the server error message, if any.
   *
   * @return Server error message, or null
   */
  public String getServerErrorMessage() {
    return srvErrMessage;
  }

  /**
   * Set the server error message, if any.
   *
   * @param errMessage Explanatory String returned by the server
   * @return a LockssRestHttpException with this object.
   */
  public LockssRestHttpException setServerErrorMessage(String errMessage) {
    this.srvErrMessage = errMessage;
    return this;
  }

  /**
   * Return the server error Map, if any.
   *
   * @return Map containing error details, or null
   */
  public Map getServerErrorMap() {
    return srvErrMap;
  }

  /**
   * Set the server error Map
   *
   * @param errMap Explanatory Map returned by the server
   * @return a LockssRestHttpException with this object.
   */
  public LockssRestHttpException setServerErrorMap(Map errMap) {
    this.srvErrMap = errMap;
    return this;
  }

  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append(getHttpStatusCode());
    sb.append(" ");
    sb.append(getHttpStatusMessage());

//    String message = super.getMessage();
    String message = contextMessage;

    if (message != null) {
      sb.append(": ");
      sb.append(message);
    }
    return sb.toString();
  }

  public void setMessage(String message) {
    contextMessage = message;
  }
}

