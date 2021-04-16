/*

 Copyright (c) 2019-2021 Board of Trustees of Leland Stanford Jr. University,
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

import org.lockss.log.L4JLogger;
import org.lockss.util.rest.RestResponseErrorBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A subclass of {@link LockssRestException} representing HTTP error responses returned
 * by LOCKSS Spring Boot applications.
 */
public class LockssRestHttpException extends LockssRestException {
  private static final long serialVersionUID = -4151454747192096531L;
  private static L4JLogger log = L4JLogger.getLogger();

  /**
   * HTTP response status of error.
   */
  private HttpStatus httpStatus;

  /**
   * Error response headers.
   */
  private HttpHeaders responseHeaders;

  /**
   * Server error message.
   */
  private String serverErrorMessage;

  /**
   * A {@link RestResponseErrorBody.RestResponseError} representing the HTTP response.
   *
   */
  private RestResponseErrorBody.RestResponseError restResponseError;

  /**
   * Type of error experienced by the server (for 5xx series of errors). See {@link ServerErrorType}.
   */
  private ServerErrorType serverErrorType = ServerErrorType.NONE;

  /**
   * Client error message from RestTemplate call.
   */
  private String clientErrorMessage;

  /**
   * Holds the exception that was thrown if there was an error parsing the error response body.
   */
  private Exception parseException;

  /**
   * Enum help distinguish types of 5xx series HTTP errors experienced by LOCKSS Spring Boot applications.
   *
   * <ul>
   *   <li>{@code NONE}: Not a server error.</li>
   *   <li>{@code UNSPECIFIED_ERROR}: Default if not specified by REST application.</li>
   *   <li>{@code APPLICATION_ERROR}: Errors which occurred with the Spring Boot application itself.</li>
   *   <li>{@code DATA_ERROR}: Errors which occurred because of an irrecoverable problem with the data.</li>
   * </ul>
   */
  public enum ServerErrorType {
    NONE,
    UNSPECIFIED_ERROR,
    APPLICATION_ERROR,
    DATA_ERROR,
  }

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
   * @param responseHeaders
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
    return serverErrorMessage;
  }

  /**
   * Set the server error message, if any.
   *
   * @param errMessage Explanatory String returned by the server
   * @return a LockssRestHttpException with this object.
   */
  public LockssRestHttpException setServerErrorMessage(String errMessage) {
    this.serverErrorMessage = errMessage;
    return this;
  }

  /**
   * Returns the error response parsing exception, if any.
   *
   * @return An
   */
  public Exception getParseException() {
    return parseException;
  }

  public void setParseException(Exception parseException) {
    this.parseException = parseException;
  }

  /**
   * Return a {@link org.lockss.util.rest.RestResponseErrorBody.RestResponseError} containing
   * the response error details, if any.
   *
   * @return
   */
  public RestResponseErrorBody.RestResponseError getRestResponseError() {
    return restResponseError;
  }

  public LockssRestHttpException setRestResponseError(RestResponseErrorBody.RestResponseError error) {
    this.restResponseError = error;
    return this;
  }

  /**
   * Returns the type of server error reported by the server, if any.
   *
   * @return The {@link ServerErrorType} reported by the server.
   */
  public ServerErrorType getServerErrorType() {
    return serverErrorType;
  }

  /**
   * Sets the server error type.
   *
   * @param serverErrorType A {@link ServerErrorType}.
   */
  public void setServerErrorType(ServerErrorType serverErrorType) {
    this.serverErrorType = serverErrorType;
  }

  /**
   * Transforms a {@link RestClientResponseException} to a new {@link LockssRestHttpException} using the first
   * {@link HttpMessageConverter} capable of reading the error response body, from a list of provided
   * {@link HttpMessageConverter}s.
   *
   * @param e1                The {@link RestClientResponseException} to transform.
   * @param messageConverters A {@link List <HttpMessageConverter>} to try to read the error response body with.
   * @return A {@link LockssRestHttpException} representing the {@link RestClientResponseException}.
   */
  public static LockssRestHttpException fromRestClientResponseException(
      RestClientResponseException e1, List<HttpMessageConverter<?>> messageConverters) {

    log.debug2("errorResponseBody = {}", e1.getResponseBodyAsString());

    // LockssRestHttpException to populate and return
    LockssRestHttpException lrhe = new LockssRestHttpException();

    // Populate LockssRestHttpException with base information
    lrhe.setHttpStatus(HttpStatus.valueOf(e1.getRawStatusCode()));
    lrhe.setHttpResponseHeaders(e1.getResponseHeaders());
//    lrhe.setServerErrorMessage(e1.getMessage());

    boolean additionalDetailsAdded = false;

    // Iterate over HTTP message converters to parse error response body (both 4xx and 5xx errors)
    for (HttpMessageConverter converter : messageConverters) {
      // Check if this HTTP message converter can deserialize to RestResponseErrorBody
      if (converter.canRead(
          RestResponseErrorBody.RestResponseError.class,
          e1.getResponseHeaders().getContentType())) {

        try {
          // Attempt to deserialize the input into a RestResponseError object
          RestResponseErrorBody.RestResponseError responseError =
              (RestResponseErrorBody.RestResponseError) converter.read(
                  RestResponseErrorBody.RestResponseError.class,
                  // ByteArrayHttpInputMessage is used here instead of the provided ClientHttpResponse to avoid
                  // problems of successive HttpMessageConverters reading from a possibly exhausted InputStream
                  // in ClientHttpResponse
                  new ByteArrayHttpInputMessage(e1.getResponseHeaders(), e1.getResponseBodyAsByteArray())
              );

          // Create and populate a new LockssRestHttpException
          lrhe.setRestResponseError(responseError);
          lrhe.setServerErrorMessage(responseError.getMessage());
          lrhe.setServerErrorType(responseError.getServerErrorType());

          additionalDetailsAdded = true;
          break;

        } catch (IOException | HttpMessageNotReadableException e2) {
          // An error occurred while reading or parsing the error response body
          log.error("An error occurred while reading or parsing the error response body with {}",
              converter.getClass().getSimpleName(), e2);

          // Save the parsing error in the LRHE
          lrhe.setParseException(e2);

          // Abort error response body parsing and allow method to return a minimally populated LRHE
          break;
        }
      }
    }

    if (!additionalDetailsAdded) {
      lrhe.setServerErrorMessage(e1.getResponseBodyAsString());
    }

    // If we are processing a 5xx error and the server error type was not
    // specified (default in LRHE is NONE), set it to UNSPECIFIED_ERROR:
    if (lrhe.getHttpStatus().is5xxServerError() &&
        lrhe.getServerErrorType() == ServerErrorType.NONE) {
      lrhe.setServerErrorType(ServerErrorType.UNSPECIFIED_ERROR);
    }

    return lrhe;
  }

  /**
   * Byte array implementation of {@link HttpInputMessage}.
   * <p>
   * This was written as a workaround for successive {@link HttpMessageConverter}s from reading from the same and
   * possibly exhausted {@link InputStream} in {@link ClientHttpResponse}.
   * <p>
   * TODO: Move to lockss-util? Or lockss-spring-bundle?
   */
  public static class ByteArrayHttpInputMessage implements HttpInputMessage {
    private final HttpHeaders headers;
    private final byte[] inputMessageBytes;

    public ByteArrayHttpInputMessage(HttpHeaders headers, byte[] inputMessageBytes) {
      this.headers = headers;
      this.inputMessageBytes = inputMessageBytes;
    }

    @Override
    public InputStream getBody() throws IOException {
      return new ByteArrayInputStream(inputMessageBytes);
    }

    @Override
    public HttpHeaders getHeaders() {
      return headers;
    }
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append(getHttpStatusCode());
    sb.append(" ");
    sb.append(getHttpStatusMessage());

//    String message = super.getMessage();
    String message = clientErrorMessage;

    if (message != null) {
      sb.append(": ");
      sb.append(message);
    }
    return sb.toString();
  }

  /**
   * Sets the client error message, if any.
   *
   * @param message A {@link String} containing the client error message.
   */
  public void setClientErrorMessage(String message) {
    clientErrorMessage = message;
  }
}

