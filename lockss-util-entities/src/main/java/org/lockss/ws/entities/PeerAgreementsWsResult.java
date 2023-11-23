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

import java.util.Map;
import java.util.Objects;

/**
 * Container for the information related to an archival unit poll agreements
 * with another peer that is the result of a query.
 */
public class PeerAgreementsWsResult {
  private String peerId;
  private Map<AgreementTypeWsResult, PeerAgreementWsResult> agreements;

  /**
   * Provides the identifier of the other peer in the agreement.
   * 
   * @return a String with the identifier.
   */
  public String getPeerId() {
    return peerId;
  }
  public void setPeerId(String peerId) {
    this.peerId = peerId;
  }

  /**
   * Provides the data about the agreements with the other peer.
   * 
   * @return a {@code Map<AgreementTypeWsResult, PeerAgreementWsResult>} with the
   *         agreements data.
   */
  public Map<AgreementTypeWsResult, PeerAgreementWsResult> getAgreements() {
    return agreements;
  }
  public void setAgreements(
      Map<AgreementTypeWsResult, PeerAgreementWsResult> agreements) {
    this.agreements = agreements;
  }

  @Override
  public String toString() {
    return "PeerAgreementsWsResult [peerId=" + peerId + ", agreements="
	+ agreements + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PeerAgreementsWsResult that = (PeerAgreementsWsResult) o;
    return Objects.equals(peerId, that.peerId) && Objects.equals(agreements, that.agreements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(peerId, agreements);
  }
}
