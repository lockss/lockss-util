/*
 * Copyright (c) 2018, Board of Trustees of Leland Stanford Jr. University,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.lockss.util.rest;

import com.fasterxml.jackson.annotation.JsonValue;
import org.lockss.util.rest.exception.LockssRestHttpException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The body of a REST error response conforming to the media type
 * {@code application/vnd.error}.
 */
// The root element name as per the {@code application/vnd.error} specification.
@XmlRootElement(name = "errorResponse")
public class RestResponseErrorBody
    implements Iterable<RestResponseErrorBody.RestResponseError> {

  // The element name of individual errors.
  @XmlElement(name = "error")
  @XmlElementWrapper(name = "errors")
  private List<RestResponseError> errors;

  /**
   * Protected default constructor to allow JAXB marshalling.
   */
  public RestResponseErrorBody() {
    this.errors = new ArrayList<RestResponseError>();
  }

  public RestResponseErrorBody(List<RestResponseError> errors) {
    this.errors = errors;
  }

  /**
   * Constructor.
   *
   * @param message A String with the detail message.
   * @param path A String with a copy of the parsed HTTP request contents.
   */
  public RestResponseErrorBody(String message, String path) {
    this(new RestResponseError(message, path));
  }

  /**
   * Constructor.
   *
   * @param message A String with the detail message.
   * @param path A String with a copy of the parsed HTTP request contents.
   * @param timestamp A LocalDateTime with the exception date and time.
   */
  public RestResponseErrorBody(String message, String path,
                               LocalDateTime timestamp) {
    this(new RestResponseError(message, path, timestamp));
  }

  /**
   * Constructor.
   *
   * @param error An Error to be included in the body.
   */
  public RestResponseErrorBody(RestResponseError error) {
    this.errors = new ArrayList<RestResponseError>();

    if (error != null) {
      this.errors.add(error);
    }
  }

  /**
   * Adds an error.
   *
   * @param error A RestResponseError with the error to be added.
   * @return a RestResponseErrorBody with this object.
   */
  public RestResponseErrorBody add(RestResponseError error) {
    this.errors.add(error);
    return this;
  }

  /**
   * Dummy method to allow JsonValue to be configured.
   */
  @JsonValue
  public List<RestResponseError> getErrors() {
    return errors;
  }

  public void setErrors(List<RestResponseError> errors) {
    this.errors = errors;
  }

  @Override
  public Iterator<RestResponseError> iterator() {
    return this.errors.iterator();
  }

  @Override
  public String toString() {
    return String.format("Errors[%s]", errors);
  }

  @Override
  public int hashCode() {
    return errors.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof RestResponseErrorBody)) {
      return false;
    }

    RestResponseErrorBody other = (RestResponseErrorBody) obj;
    return this.errors.equals(other.errors);
  }

  /**
   * A single error.
   *
   * The fields in this class are intended to be a superset of the set of fields generated by Spring's
   * {@link org.springframework.boot.autoconfigure.web.DefaultErrorAttributes}.
   */
  @XmlType
  public static class RestResponseError {
//    @XmlElement
//    @JsonProperty
    private long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

//    @XmlElement
//    @JsonProperty
    private int status;

//    @XmlElement
//    @JsonProperty
    private String error;

//    @XmlElement
//    @JsonProperty
    private String exception;

//    @XmlElement
//    @JsonProperty
    private String message;

//    @XmlElement
//    @JsonProperty
    private String path;

//    @XmlElement
//    @JsonProperty
    private LockssRestHttpException.ServerErrorType serverErrorType =
        LockssRestHttpException.ServerErrorType.NONE;

    /**
     * Protected default constructor to allow JAXB marshalling.
     */
    public RestResponseError() {
    }

    /**
     * Constructor.
     *
     * @param message A String with the detail message.
     * @param exception Name of the exception
     */
    public RestResponseError(String message, String exception) {
      if (message != null) {
        this.message = message;
      }

      if (exception != null) {
        this.exception = exception;
      }
    }

    /**
     * Constructor.
     *
     * @param message A String with the detail message.
     * @param path A String with a copy of the parsed HTTP request contents.
     * @param timestamp A LocalDateTime with the error date and time.
     */
    public RestResponseError(String message, String path,
        LocalDateTime timestamp) {
      if (message != null) {
        this.message = message;
      }

      if (path != null) {
        this.path = path;
      }

      if (timestamp != null) {
        this.timestamp = timestamp.toEpochSecond(ZoneOffset.UTC);
      }
    }

    /**
     * Provides the error message.
     *
     * @return a String with the error message.
     */
    public String getMessage() {
      return message;
    }

    public RestResponseError setMessage(String message) {
      this.message = message;
      return this;
    }

    /**
     * Provides a copy of the parsed HTTP request contents.
     *
     * @return a String with a copy of the parsed HTTP request contents.
     */
    public String getPath() {
      return path;
    }

    public RestResponseError setPath(String path) {
      this.path = path;
      return this;
    }

    public String getException() {
      return exception;
    }

    public RestResponseError setException(String exception) {
      this.exception = exception;
      return this;
    }

    /**
     * Provides the error date and time.
     *
     * @return a LocalDateTime with the error date and time.
     */
    public long getTimestamp() {
      return timestamp;
    }

    public RestResponseError setTimestamp(long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public int getStatus() {
      return status;
    }

    public RestResponseError setStatus(int status) {
      this.status = status;
      return this;
    }

    public String getError() {
      return error;
    }

    public RestResponseError setError(String error) {
      this.error = error;
      return this;
    }

    public LockssRestHttpException.ServerErrorType getServerErrorType() {
      return serverErrorType;
    }

    public RestResponseError setServerErrorType(LockssRestHttpException.ServerErrorType serverErrorType) {
      this.serverErrorType = serverErrorType;
      return this;
    }

    @Override
    public String toString() {
      return String.format("[Error message: %s, path: %s, exceptionClass: %s, "
          + "timestamp: %s]", message, path, exception, timestamp);
    }

    @Override
    public int hashCode() {
      int result = 17;

      result += 31 * message.hashCode();
      result += 31 * path.hashCode();
      result += 31 * timestamp;

      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }

      if (!(obj instanceof RestResponseError)) {
        return false;
      }

      RestResponseError other = (RestResponseError) obj;

      return this.message.equals(other.message)
          && this.path.equals(other.path)
          && this.timestamp == other.timestamp;
    }
  }
}
