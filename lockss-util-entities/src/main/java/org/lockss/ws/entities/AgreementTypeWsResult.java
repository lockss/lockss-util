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
 * Different agreement situations we record.
 * 
 * Note: in a symmetric poll the poller and voter will not necessarily record
 * the same percent agreement, since each may have content for URLs which the
 * other does not. Since the hint received after a symmetric poll is the other
 * participant's recorded percent agreement, the calculated percent agreements
 * are used to decide if a repair request should be honored, and hints are used
 * to try to find willing repairers likely to honor repair requests.
 * The enumerated values in this class need to match those in class
 * {@link org.lockss.protocol.AgreementType}.
 */
public enum AgreementTypeWsResult {
  /**
   * A poll with all content hashed and tallied. Recorded by poller.
   */
  POR,
  /**
   * A poll with a selection of the content hashed and tallied. Recorded by
   * poller.
   */
  POP,
  /**
   * A POR poll where a voter has called for the poller's hashes. Recorded by
   * voter.
   */
  SYMMETRIC_POR,
  /**
   * A POP poll where a voter has called for the poller's hashes. Recorded by
   * voter.
   */
  SYMMETRIC_POP,
  /**
   * The hint given a voter by the poller after a POR poll. Recorded by voter.
   */
  POR_HINT,
  /**
   * The hint given a voter by the poller after a POP poll. Recorded by voter.
   */
  POP_HINT,
  /**
   * The hint given a poller by a voter after a symmetric POR poll. Recorded by
   * poller.
   */
  SYMMETRIC_POR_HINT,
  /**
   * The hint given a poller by a voter after a symmetric POP poll. Recorded by
   * poller.
   */
  SYMMETRIC_POP_HINT,

  // Weighted results for each of the above poll types

  /** Weighted result of poll with all content hashed and tallied. Recorded
   * by poller. */
  W_POR,
  /** Weighted result of poll with a selection of the content hashed and
   * tallied. Recorded by poller. */
  W_POP,
  /** Weighted result of POR poll where a voter has called for the poller's
   * hashes. Recorded by voter. */
  W_SYMMETRIC_POR,
  /** Weighted result of POP poll where a voter has called for the poller's
   * hashes. Recorded by voter. */
  W_SYMMETRIC_POP,
  /** The weighted hint given a voter by the poller after a POR
   * poll. Recorded by voter. */
  W_POR_HINT,
  /** The weighted hint given a voter by the poller after a POP
   * poll. Recorded by voter. */
  W_POP_HINT,
  /** The weighted hint given a poller by a voter after a symmetric POR
   * poll. Recorded by poller. */
  W_SYMMETRIC_POR_HINT,
  /** The weighted hint given a poller by a voter after a symmetric POP
   * poll. Recorded by poller. */
  W_SYMMETRIC_POP_HINT;

}
