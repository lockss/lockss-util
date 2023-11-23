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
 * A wrapper for the result of an archival unit crawl request web service
 * operation.
 */
public class RequestCrawlResult {
  private String id;
  private boolean success = false;
  private String delayReason;
  private String errorMessage;

  /**
   * Default constructor.
   */
  public RequestCrawlResult() {
  }

  /**
   * Constructor.
   * 
   * @param id
   *          A String with the Archival Unit identifier.
   * @param success
   *          A boolean with <code>true</code> if the request was made
   *          successfully, <code>false</code> otherwise.
   * @param delayReason
   *          A String with the reason for any delay in performing the
   *          operation.
   * @param errorMessage
   *          A String with any error message as a result of the operation.
   */
  public RequestCrawlResult(String id, boolean success, String delayReason,
      String errorMessage) {
    this.id = id;
    this.success = success;
    this.delayReason = delayReason;
    this.errorMessage = errorMessage;
  }

  /**
   * Provides the Archival Unit identifier.
   * 
   * @return a String with the identifier.
   */
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Provides the indication of whether the request was made successfully.
   * 
   * @return a boolean with <code>true</code> if the request was made
   *          successfully, <code>false</code> otherwise.
   */
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Provides the reason for a in performing the operation, if any.
   * 
   * @return a String with the message.
   */
  public String getDelayReason() {
    return delayReason;
  }
  public void setDelayReason(String delayReason) {
    this.delayReason = delayReason;
  }

  /**
   * Provides any error message as a result of the operation.
   * 
   * @return a String with the error message.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String toString() {
    return "[RequestCrawlResult id=" + id + ", success=" + success
	+ ", delayReason=" + delayReason + ", errorMessage=" + errorMessage
	+ "]";
  }
}
