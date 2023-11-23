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

package org.lockss.ws.entities;

/**
 * A wrapper for the result of a Metadata Control web service operation.
 */
public class MetadataControlResult {
  private Boolean isSuccess;
  private String message;

  /**
   * Default constructor.
   */
  public MetadataControlResult() {
  }

  /**
   * Constructor.
   * 
   * @param isSuccess
   *          A Boolean with the indication of whether the operation was
   *          successful.
   * @param message
   *          A String with a descriptive message of the result of the
   *          operation.
   */
  public MetadataControlResult(Boolean isSuccess, String message) {
    this.isSuccess = isSuccess;
    this.message = message;
  }

  /**
   * Provides an indication of whether the operation was successful.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getIsSuccess() {
    return isSuccess;
  }
  public void setIsSuccess(Boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  /**
   * Provides a descriptive message of the result of the operation.
   * 
   * @return a String with the message.
   */
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "[MetadataControlResult isSuccess=" + isSuccess + ", message="
	+ message + "]";
  }
}
