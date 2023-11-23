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

import java.util.*;
import com.fasterxml.jackson.annotation.*;

/**
 * Container for the information related to a poll participant that is the
 * result of a query.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipantWsResult {
  private String peerId;
  private String peerStatus;
  private Boolean hasVoted;
  private Float percentAgreement;
  private Long agreedVoteCount;
  private Long disagreedVoteCount;
  private Long pollerOnlyVoteCount;
  private Long voterOnlyVotecount;
  private boolean isPostRepair = false;

  private Collection<String> agreedUrls;
  private Collection<String> disagreedUrls;
  private Collection<String> pollerOnlyUrls;
  private Collection<String> voterOnlyUrls;

  private Long bytesHashed;
  private Long bytesRead;
  private String currentState;
  private Long lastStateChange;
  private Boolean isExParticipant;

  /**
   * Provides the peer identifier.
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
   * Provides the peer status.
   * 
   * @return a String with the status.
   */
  public String getPeerStatus() {
    return peerStatus;
  }
  public void setPeerStatus(String peerStatus) {
    this.peerStatus = peerStatus;
  }

  /**
   * Provides an indication of whether the participant has voted.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getHasVoted() {
    return hasVoted;
  }
  public void setHasVoted(Boolean hasVoted) {
    this.hasVoted = hasVoted;
  }

  /**
   * True if the percent agreement and URL counts/lists are updated with
   * the results of repairs
   */
  public Boolean isPostRepair() {
    return isPostRepair;
  }

  public Boolean getIsPostRepair() {
    return isPostRepair;
  }

  public void setIsPostRepair(Boolean isPostRepair) {
    this.isPostRepair = isPostRepair;
  }

  /**
   * Provides the participant agreement percentage.
   * 
   * @return a Float with the agreement percentage.
   */
  public Float getPercentAgreement() {
    return percentAgreement;
  }
  public void setPercentAgreement(Float percentAgreement) {
    this.percentAgreement = percentAgreement;
  }

  /**
   * Provides the count of votes in agreement.
   * 
   * @return a Long with the count.
   */
  public Long getAgreedVoteCount() {
    return agreedVoteCount;
  }
  public void setAgreedVoteCount(Long agreedVoteCount) {
    this.agreedVoteCount = agreedVoteCount;
  }

  /**
   * Provides the count of votes in disagreement.
   * 
   * @return a Long with the count.
   */
  public Long getDisagreedVoteCount() {
    return disagreedVoteCount;
  }
  public void setDisagreedVoteCount(Long disagreedVoteCount) {
    this.disagreedVoteCount = disagreedVoteCount;
  }

  /**
   * Provides the count of URLs that the poller has but this peer doesn't.
   * 
   * @return a Long with the count.
   */
  public Long getPollerOnlyVoteCount() {
    return pollerOnlyVoteCount;
  }
  public void setPollerOnlyVoteCount(Long pollerOnlyVoteCount) {
    this.pollerOnlyVoteCount = pollerOnlyVoteCount;
  }

  /**
   * Provides the count of URLs that this voter has but the poller doesn't.
   * 
   * @return a Long with the count.
   */
  public Long getVoterOnlyVotecount() {
    return voterOnlyVotecount;
  }
  public void setVoterOnlyVotecount(Long voterOnlyVotecount) {
    this.voterOnlyVotecount = voterOnlyVotecount;
  }

  /**
   * Provides the list of agreeing URLs.
   *
   * @return a {@code Collection<String>} with the agreeing URLs.
   */
  public Collection<String> getAgreedUrls() {
    return agreedUrls;
  }
  public void setAgreedUrls(Collection<String> agreedUrls) {
    this.agreedUrls = agreedUrls;
  }

  /**
   * Provides the list of disagreeing URLs.
   *
   * @return a {@code Collection<String>} with the disagreeing URLs.
   */
  public Collection<String> getDisagreedUrls() {
    return disagreedUrls;
  }
  public void setDisagreedUrls(Collection<String> disagreedUrls) {
    this.disagreedUrls = disagreedUrls;
  }

  /**
   * Provides the list of pollerOnly URLs.
   *
   * @return a {@code Collection<String>} with the pollerOnly URLs.
   */
  public Collection<String> getPollerOnlyUrls() {
    return pollerOnlyUrls;
  }
  public void setPollerOnlyUrls(Collection<String> pollerOnlyUrls) {
    this.pollerOnlyUrls = pollerOnlyUrls;
  }

  /**
   * Provides the list of voterOnly URLs.
   *
   * @return a {@code Collection<String>} with the voterOnly URLs.
   */
  public Collection<String> getVoterOnlyUrls() {
    return voterOnlyUrls;
  }
  public void setVoterOnlyUrls(Collection<String> voterOnlyUrls) {
    this.voterOnlyUrls = voterOnlyUrls;
  }

  /**
   * Provides the count of bytes hashed.
   * 
   * @return a Long with the byte count.
   */
  public Long getBytesHashed() {
    return bytesHashed;
  }
  public void setBytesHashed(Long bytesHashed) {
    this.bytesHashed = bytesHashed;
  }

  /**
   * Provides the count of bytes read.
   * 
   * @return a Long with the byte count.
   */
  public Long getBytesRead() {
    return bytesRead;
  }
  public void setBytesRead(Long bytesRead) {
    this.bytesRead = bytesRead;
  }

  /**
   * Provides the participant current state.
   * 
   * @return a String with the current state.
   */
  public String getCurrentState() {
    return currentState;
  }
  public void setCurrentState(String currentState) {
    this.currentState = currentState;
  }

  /**
   * Provides the timestamp of the last state change.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getLastStateChange() {
    return lastStateChange;
  }
  public void setLastStateChange(Long lastStateChange) {
    this.lastStateChange = lastStateChange;
  }

  /**
   * Provides an indication of whether the peer is an ex-participant.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getIsExParticipant() {
    return isExParticipant;
  }
  public void setIsExParticipant(Boolean isExParticipant) {
    this.isExParticipant = isExParticipant;
  }

  @Override
  public String toString() {
    return "ParticipantWsResult [peerId=" + peerId + ", peerStatus="
	+ peerStatus + ", hasVoted=" + hasVoted + ", percentAgreement="
	+ percentAgreement + ", agreedVoteCount=" + agreedVoteCount
	+ ", disagreedVoteCount=" + disagreedVoteCount
	+ ", pollerOnlyVoteCount=" + pollerOnlyVoteCount
	+ ", voterOnlyVotecount=" + voterOnlyVotecount + ", bytesHashed="
	+ bytesHashed + ", bytesRead=" + bytesRead + ", currentState="
	+ currentState + ", lastStateChange=" + lastStateChange
	+ ", isExParticipant=" + isExParticipant + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParticipantWsResult that = (ParticipantWsResult) o;
    return isPostRepair == that.isPostRepair &&
        Objects.equals(peerId, that.peerId) &&
        Objects.equals(peerStatus, that.peerStatus) &&
        Objects.equals(hasVoted, that.hasVoted) &&
        Objects.equals(percentAgreement, that.percentAgreement) &&
        Objects.equals(agreedVoteCount, that.agreedVoteCount) &&
        Objects.equals(disagreedVoteCount, that.disagreedVoteCount) &&
        Objects.equals(pollerOnlyVoteCount, that.pollerOnlyVoteCount) &&
        Objects.equals(voterOnlyVotecount, that.voterOnlyVotecount) &&
        Objects.equals(agreedUrls, that.agreedUrls) &&
        Objects.equals(disagreedUrls, that.disagreedUrls) &&
        Objects.equals(pollerOnlyUrls, that.pollerOnlyUrls) &&
        Objects.equals(voterOnlyUrls, that.voterOnlyUrls) &&
        Objects.equals(bytesHashed, that.bytesHashed) &&
        Objects.equals(bytesRead, that.bytesRead) &&
        Objects.equals(currentState, that.currentState) &&
        Objects.equals(lastStateChange, that.lastStateChange) &&
        Objects.equals(isExParticipant, that.isExParticipant);
  }

//  @Override
//  public int hashCode() {
//    return Objects.hash(peerId, peerStatus, hasVoted, percentAgreement, agreedVoteCount, disagreedVoteCount,
//        pollerOnlyVoteCount, voterOnlyVotecount, isPostRepair, agreedUrls, disagreedUrls, pollerOnlyUrls, voterOnlyUrls,
//        bytesHashed, bytesRead, currentState, lastStateChange, isExParticipant);
//  }
}
