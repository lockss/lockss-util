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

package org.lockss.util.rest.exception;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.regex.*;

public class LockssRestNetworkException extends LockssRestException {
  private static final long serialVersionUID = 2600539944608507147L;

  /**
   * Default constructor.
   */
  public LockssRestNetworkException() {
    super();
  }

  /**
   * Constructor with a specified message.
   * 
   * @param message
   *          A String with the exception message.
   */
  public LockssRestNetworkException(String message) {
    super(message);
  }

  /**
   * Constructor with a specified cause.
   * 
   * @param cause
   *          A Throwable with the exception cause.
   */
  public LockssRestNetworkException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor with specified message and cause.
   * 
   * @param message
   *          A String with the exception message.
   * @param cause
   *          A Throwable with the exception cause.
   */
  public LockssRestNetworkException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Return a shortened exception message */
  public String getShortMessage() {
    Throwable cause = getCause();
    if (cause instanceof java.net.ConnectException) {
      return cleanupExceptionMessage(cause.getMessage());
    }
    if (cause != null) {
      return ExceptionUtils.getRootCauseMessage(cause);
    }
    return getMessage();
  }

  // Clean up ugliness like "Connection refused (Connection refused)"
  protected static final Pattern DUP_MSG_PAT =
    Pattern.compile("(.*)(.*) ?\\(\\2\\)(.*)");

  String cleanupExceptionMessage(String msg) {
    Matcher mat = DUP_MSG_PAT.matcher(msg);
    if (mat.matches()) {
      return mat.group(1) + mat.group(2) + mat.group(3);
    }
    return msg;
  }
}
