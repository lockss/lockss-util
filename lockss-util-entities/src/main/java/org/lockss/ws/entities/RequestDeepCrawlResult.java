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
 * A wrapper for the result of an archival unit deep crawl request web service
 * operation.
 */
public class RequestDeepCrawlResult extends RequestCrawlResult {
  private int refetchDepth;

  /**
   * Default constructor.
   */
  public RequestDeepCrawlResult() {
  }

  /**
   * Constructor.
   * 
   * @param id
   *          A String with the Archival Unit identifier.
   * @param refetchDepth
   *          An int with the refetch depth of the crawl.
   * @param success
   *          A boolean with <code>true</code> if the request was made
   *          successfully, <code>false</code> otherwise.
   * @param delayReason
   *          A String with the reason for any delay in performing the
   *          operation.
   * @param errorMessage
   *          A String with any error message as a result of the operation.
   */
  public RequestDeepCrawlResult(String id, int refetchDepth, boolean success,
      String delayReason, String errorMessage) {
    setId(id);
    setSuccess(success);
    setDelayReason(delayReason);
    setErrorMessage(errorMessage);
    this.refetchDepth = refetchDepth;
  }

  /**
   * Provides the crawl refetch depth.
   * 
   * @return an int with the crawl refetch depth.
   */
  public int getRefetchDepth() {
    return refetchDepth;
  }
  public void setRefetchDepth(int refetchDepth) {
    this.refetchDepth = refetchDepth;
  }

  @Override
  public String toString() {
    return "[RequestDeepCrawlResult: refetchDepth=" + refetchDepth + ", "
	+ super.toString() + "]";
  }
}
