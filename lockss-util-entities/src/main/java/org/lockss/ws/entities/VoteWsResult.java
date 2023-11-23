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
 * Container for the information related to a vote that is the result of a
 * query.
 */
public class VoteWsResult {
  private String auId;
  private String auName;
  private String callerId;
  private String voteStatus;
  private Long startTime;
  private Long deadline;
  private String voteKey;
  private Boolean isPollActive;
  private String currentState;
  private String errorDetail;
  private Long voteDeadline;
  private Long duration;
  private Long remainingTime;
  private Double agreementHint;
  private String pollerNonce;
  private String voterNonce;
  private String voterNonce2;
  private Boolean isSymmetricPoll;
  private Integer agreedUrlCount;
  private Integer disagreedUrlCount;
  private Integer pollerOnlyUrlCount;
  private Integer voterOnlyUrlCount;

  /**
   * Provides the Archival Unit identifier.
   * 
   * @return a String with the identifier.
   */
  public String getAuId() {
    return auId;
  }
  public void setAuId(String auId) {
    this.auId = auId;
  }

  /**
   * Provides the Archival Unit name.
   * 
   * @return a String with the name.
   */
  public String getAuName() {
    return auName;
  }
  public void setAuName(String auName) {
    this.auName = auName;
  }

  /**
   * Provides the caller identifier.
   * 
   * @return a String with the identifier.
   */
  public String getCallerId() {
    return callerId;
  }
  public void setCallerId(String callerId) {
    this.callerId = callerId;
  }

  /**
   * Provides the vote status.
   * 
   * @return a String with the status.
   */
  public String getVoteStatus() {
    return voteStatus;
  }
  public void setVoteStatus(String voteStatus) {
    this.voteStatus = voteStatus;
  }

  /**
   * Provides the vote start timestamp.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getStartTime() {
    return startTime;
  }
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  /**
   * Provides the vote deadline timestamp.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getDeadline() {
    return deadline;
  }
  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  /**
   * Provides the vote key.
   * 
   * @return a String with the key.
   */
  public String getVoteKey() {
    return voteKey;
  }
  public void setVoteKey(String voteKey) {
    this.voteKey = voteKey;
  }

  /**
   * Provides an indication of whether the poll is active.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getIsPollActive() {
    return isPollActive;
  }
  public void setIsPollActive(Boolean isPollActive) {
    this.isPollActive = isPollActive;
  }

  /**
   * Provides the vote current state.
   * 
   * @return a String with the state.
   */
  public String getCurrentState() {
    return currentState;
  }
  public void setCurrentState(String currentState) {
    this.currentState = currentState;
  }

  /**
   * Provides the vote error detail.
   * 
   * @return a String with the error detail.
   */
  public String getErrorDetail() {
    return errorDetail;
  }
  public void setErrorDetail(String errorDetail) {
    this.errorDetail = errorDetail;
  }

  /**
   * Provides the vote deadline timestamp.
   * 
   * @return a Long with the timestamp as the number of milliseconds since the
   *         beginning of 1970.
   */
  public Long getVoteDeadline() {
    return voteDeadline;
  }
  public void setVoteDeadline(Long voteDeadline) {
    this.voteDeadline = voteDeadline;
  }

  /**
   * Provides the vote duration.
   * 
   * @return a Long with the duration in milliseconds.
   */
  public Long getDuration() {
    return duration;
  }
  public void setDuration(Long duration) {
    this.duration = duration;
  }

  /**
   * Provides the vote remaining time.
   * 
   * @return a Long with the remaining time in milliseconds.
   */
  public Long getRemainingTime() {
    return remainingTime;
  }
  public void setRemainingTime(Long remainingTime) {
    this.remainingTime = remainingTime;
  }

  /**
   * Provides the vote agreement hint.
   * 
   * @return a Double with the agreement hint.
   */
  public Double getAgreementHint() {
    return agreementHint;
  }
  public void setAgreementHint(Double agreementHint) {
    this.agreementHint = agreementHint;
  }

  /**
   * Provides the poller nonce.
   * 
   * @return a String with the poller nonce.
   */
  public String getPollerNonce() {
    return pollerNonce;
  }
  public void setPollerNonce(String pollerNonce) {
    this.pollerNonce = pollerNonce;
  }

  /**
   * Provides the voter nonce.
   * 
   * @return a String with the voter nonce.
   */
  public String getVoterNonce() {
    return voterNonce;
  }
  public void setVoterNonce(String voterNonce) {
    this.voterNonce = voterNonce;
  }

  /**
   * Provides the symmetric poll voter nonce.
   * 
   * @return a String with the symmetric poll voter nonce.
   */
  public String getVoterNonce2() {
    return voterNonce2;
  }
  public void setVoterNonce2(String voterNonce2) {
    this.voterNonce2 = voterNonce2;
  }

  /**
   * Provides an indication of whether the poll is symmetric.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean getIsSymmetricPoll() {
    return isSymmetricPoll;
  }
  public void setIsSymmetricPoll(Boolean isSymmetricPoll) {
    this.isSymmetricPoll = isSymmetricPoll;
  }

  /**
   * Provides the count of URLs in agreement.
   * 
   * @return an Integer with the count.
   */
  public Integer getAgreedUrlCount() {
    return agreedUrlCount;
  }
  public void setAgreedUrlCount(Integer agreedUrlCount) {
    this.agreedUrlCount = agreedUrlCount;
  }

  /**
   * Provides the count of URLs in disagreement.
   * 
   * @return an Integer with the count.
   */
  public Integer getDisagreedUrlCount() {
    return disagreedUrlCount;
  }
  public void setDisagreedUrlCount(Integer disagreedUrlCount) {
    this.disagreedUrlCount = disagreedUrlCount;
  }

  /**
   * Provides the count of poller-only URLs.
   * 
   * @return an Integer with the count.
   */
  public Integer getPollerOnlyUrlCount() {
    return pollerOnlyUrlCount;
  }
  public void setPollerOnlyUrlCount(Integer pollerOnlyUrlCount) {
    this.pollerOnlyUrlCount = pollerOnlyUrlCount;
  }

  /**
   * Provides the count of voter-only URLs.
   * 
   * @return an Integer with the count.
   */
  public Integer getVoterOnlyUrlCount() {
    return voterOnlyUrlCount;
  }
  public void setVoterOnlyUrlCount(Integer voterOnlyUrlCount) {
    this.voterOnlyUrlCount = voterOnlyUrlCount;
  }

  @Override
  public String toString() {
    return "VoteWsResult [auId=" + auId + ", auName=" + auName + ", callerId="
	+ callerId + ", voteStatus=" + voteStatus + ", startTime=" + startTime
	+ ", deadline=" + deadline + ", voteKey=" + voteKey + ", isPollActive="
	+ isPollActive + ", currentState=" + currentState + ", errorDetail="
	+ errorDetail + ", voteDeadline=" + voteDeadline + ", duration="
	+ duration + ", remainingTime=" + remainingTime + ", agreementHint="
	+ agreementHint + ", pollerNonce=" + pollerNonce + ", voterNonce="
	+ voterNonce + ", voterNonce2=" + voterNonce2 + ", isSymmetricPoll="
	+ isSymmetricPoll + ", agreedUrlCount=" + agreedUrlCount
	+ ", disagreedUrlCount=" + disagreedUrlCount + ", pollerOnlyUrlCount="
	+ pollerOnlyUrlCount + ", voterOnlyUrlCount=" + voterOnlyUrlCount + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VoteWsResult that = (VoteWsResult) o;
    return Objects.equals(auId, that.auId) &&
        Objects.equals(auName, that.auName) &&
        Objects.equals(callerId, that.callerId) &&
        Objects.equals(voteStatus, that.voteStatus) &&
        Objects.equals(startTime, that.startTime) &&
        Objects.equals(deadline, that.deadline) &&
        Objects.equals(voteKey, that.voteKey) &&
        Objects.equals(isPollActive, that.isPollActive) &&
        Objects.equals(currentState, that.currentState) &&
        Objects.equals(errorDetail, that.errorDetail) &&
        Objects.equals(voteDeadline, that.voteDeadline) &&
        Objects.equals(duration, that.duration) &&
        Objects.equals(remainingTime, that.remainingTime) &&
        Objects.equals(agreementHint, that.agreementHint) &&
        Objects.equals(pollerNonce, that.pollerNonce) &&
        Objects.equals(voterNonce, that.voterNonce) &&
        Objects.equals(voterNonce2, that.voterNonce2) &&
        Objects.equals(isSymmetricPoll, that.isSymmetricPoll) &&
        Objects.equals(agreedUrlCount, that.agreedUrlCount) &&
        Objects.equals(disagreedUrlCount, that.disagreedUrlCount) &&
        Objects.equals(pollerOnlyUrlCount, that.pollerOnlyUrlCount) &&
        Objects.equals(voterOnlyUrlCount, that.voterOnlyUrlCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, auName, callerId, voteStatus, startTime, deadline, voteKey, isPollActive, currentState,
        errorDetail, voteDeadline, duration, remainingTime, agreementHint, pollerNonce, voterNonce, voterNonce2,
        isSymmetricPoll, agreedUrlCount, disagreedUrlCount, pollerOnlyUrlCount, voterOnlyUrlCount);
  }
}
