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

import java.util.Objects;

/**
 * Container for the information related to an archival unit poll agreement
 * that is the result of a query.
 */
public class PeerAgreementWsResult {
  private Float percentAgreement;
  private Long percentAgreementTimestamp;
  private Float highestPercentAgreement;
  private Long highestPercentAgreementTimestamp;

  /**
   * No-argument constructor required by CXF.
   */
  public PeerAgreementWsResult() {
    
  }

  /**
   * Constructor.
   * 
   * @param percentAgreement
   *          A Float with the most recent agreement percentage.
   * @param percentAgreementTimestamp
   *          A Long with the timestamp of the most recent agreement percentage.
   * @param highestPercentAgreement
   *          A Float with the highest-ever agreement percentage.
   * @param highestPercentAgreementTimestamp
   *          A Long with the timestamp of the highest-ever agreement
   *          percentage.
   */
  public PeerAgreementWsResult(
    Float percentAgreement, Long percentAgreementTimestamp,
    Float highestPercentAgreement, Long highestPercentAgreementTimestamp) {
    this.percentAgreement = percentAgreement;
    this.percentAgreementTimestamp = percentAgreementTimestamp;
    this.highestPercentAgreement = highestPercentAgreement;
    this.highestPercentAgreementTimestamp = highestPercentAgreementTimestamp;
  }

  /**
   * Provides the most recent agreement percentage.
   * 
   * @return a Float with the percentage.
   */
  public Float getPercentAgreement() {
    return percentAgreement;
  }
  public void setPercentAgreement(Float percentAgreement) {
    this.percentAgreement = percentAgreement;
  }

  /**
   * Provides the timestamp of the most recent agreement percentage.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getPercentAgreementTimestamp() {
    return percentAgreementTimestamp;
  }
  public void setPercentAgreementTimestamp(Long percentAgreementTimestamp) {
    this.percentAgreementTimestamp = percentAgreementTimestamp;
  }

  /**
   * Provides the highest-ever agreement percentage.
   * 
   * @return a Float with the percentage.
   */
  public Float getHighestPercentAgreement() {
    return highestPercentAgreement;
  }
  public void setHighestPercentAgreement(Float highestPercentAgreement) {
    this.highestPercentAgreement = highestPercentAgreement;
  }

  /**
   * Provides the timestamp of the highest-ever agreement percentage.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getHighestPercentAgreementTimestamp() {
    return highestPercentAgreementTimestamp;
  }
  public void setHighestPercentAgreementTimestamp(
      Long highestPercentAgreementTimestamp) {
    this.highestPercentAgreementTimestamp = highestPercentAgreementTimestamp;
  }

  @Override
  public String toString() {
    return "PeerAgreementWsResult [percentAgreement=" + percentAgreement
	+ ", percentAgreementTimestamp=" + percentAgreementTimestamp
	+ ", highestPercentAgreement=" + highestPercentAgreement
	+ ", highestPercentAgreementTimestamp="
	+ highestPercentAgreementTimestamp + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PeerAgreementWsResult that = (PeerAgreementWsResult) o;
    return Objects.equals(percentAgreement, that.percentAgreement) &&
        Objects.equals(percentAgreementTimestamp, that.percentAgreementTimestamp) &&
        Objects.equals(highestPercentAgreement, that.highestPercentAgreement) &&
        Objects.equals(highestPercentAgreementTimestamp, that.highestPercentAgreementTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(percentAgreement, percentAgreementTimestamp,
        highestPercentAgreement, highestPercentAgreementTimestamp);
  }
}
