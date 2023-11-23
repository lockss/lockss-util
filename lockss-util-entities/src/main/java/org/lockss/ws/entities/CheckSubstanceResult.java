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
 * A wrapper for the result of a check substance web service operation over an
 * Archival Unit.
 */
public class CheckSubstanceResult {
  public enum State {Unknown, Yes, No};

  private String id;
  private State oldState;
  private State newState;
  private String errorMessage;

  /**
   * Default constructor.
   */
  public CheckSubstanceResult() {
  }

  /**
   * Constructor.
   * 
   * @param id
   *          A String with the Archival Unit identifier.
   * @param oldState
   *          A State with the previous substance check state.
   * @param newState
   *          A State with the current substance check state.
   * @param errorMessage
   *          A String with any error message as a result of the operation.
   */
  public CheckSubstanceResult(String id, State oldState, State newState,
      String errorMessage) {
    this.id = id;
    this.oldState = oldState;
    this.newState = newState;
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
   * Provides the previous substance check state.
   * 
   * @return a State with the previous substance check state.
   */
  public State getOldState() {
    return oldState;
  }
  public void setOldState(State oldState) {
    this.oldState = oldState;
  }

  /**
   * Provides the current substance check state.
   * 
   * @return a State with the current substance check state.
   */
  public State getNewState() {
    return newState;
  }
  public void setNewState(State newState) {
    this.newState = newState;
  }

  /**
   * Provides any error message as a result of the operation.
   * 
   * @return a String with the message.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String toString() {
    return "[CheckSubstanceResult id=" + id + ", oldState=" + oldState
	+ ", newState=" + newState + ", errorMessage=" + errorMessage + "]";
  }
}
