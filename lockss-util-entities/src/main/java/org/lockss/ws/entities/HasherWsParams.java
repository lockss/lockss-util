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
 * A wrapper for the parameters used to perform a hash operation.
 */
public class HasherWsParams {
  private String auId;
  private String url;
  private String lower;
  private String upper;
  private Boolean recordFilteredStream;
  private Boolean excludeSuspectVersions;
  private Boolean includeWeight;
  private String algorithm;
  private String hashType;
  private String resultEncoding;
  private String challenge;
  private String verifier;

  /**
   * Provides the identifier of Archival Unit to be hashed.
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
   * Provides the URL to be hashed.
   * 
   * @return a String with the URL.
   */
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  
  /**
   * Provides the URL to be hashed.
   * 
   * @return Boolean whether to include hash weight
   */
  public Boolean isIncludeWeight() {
    return includeWeight;
  }

  public void setIncludeWeight(Boolean includeWeight) {
    this.includeWeight= includeWeight;
  }

  /**
   * Provides the lower boundary URL.
   * 
   * @return a String with the URL.
   */
  public String getLower() {
    return lower;
  }
  public void setLower(String lower) {
    this.lower = lower;
  }

  /**
   * Provides the upper boundary URL.
   * 
   * @return a String with the URL.
   */
  public String getUpper() {
    return upper;
  }
  public void setUpper(String upper) {
    this.upper = upper;
  }

  /**
   * Provides an indication of whether the filtered stream should be recorded.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean isRecordFilteredStream() {
    return recordFilteredStream;
  }
  public void setRecordFilteredStream(Boolean recordFilteredStream) {
    this.recordFilteredStream = recordFilteredStream;
  }

  /**
   * Provides an indication of whether to exxclude suspect versions.
   * 
   * @return a Boolean with the indication.
   */
  public Boolean isExcludeSuspectVersions() {
    return excludeSuspectVersions;
  }
  public void setExcludeSuspectVersions(Boolean excludeSuspectVersions) {
    this.excludeSuspectVersions = excludeSuspectVersions;
  }

  /**
   * Provides the name of the hashing algorithm to be used. <br>
   * The acceptable values are SHA-1 (or SHA1), MD5 and SHA-256.
   * 
   * @return a String with the hashing algorithm name.
   */
  public String getAlgorithm() {
    return algorithm;
  }
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Provides the name of the type of hashing to be performed. <br>
   * The acceptable values are V1Content, V1Name, V1File, V3Tree and V3File.
   * 
   * @return a String with the hashing type name.
   */
  public String getHashType() {
    return hashType;
  }
  public void setHashType(String hashType) {
    this.hashType = hashType;
  }

  /**
   * Provides the name of the result encoding to be used. <br>
   * The acceptable values are Base64 and Hex.
   * 
   * @return a String with the identifier result encoding name.
   */
  public String getResultEncoding() {
    return resultEncoding;
  }
  public void setResultEncoding(String resultEncoding) {
    this.resultEncoding = resultEncoding;
  }

  /**
   * Provides the encoded challenge.
   * 
   * @return a String with the encoded challenge.
   */
  public String getChallenge() {
    return challenge;
  }
  public void setChallenge(String challenge) {
    this.challenge = challenge;
  }

  /**
   * Provides the encoded verifier.
   * 
   * @return a String with the encoded verifier.
   */
  public String getVerifier() {
    return verifier;
  }
  public void setVerifier(String verifier) {
    this.verifier = verifier;
  }
  @Override
  public String toString() {
    return "[HasherWsParams: auId=" + auId + ", url=" + url + ", lower=" + lower
	+ ", upper=" + upper + ", recordFilteredStream=" + recordFilteredStream
	+ ", excludeSuspectVersions=" + excludeSuspectVersions + ", algorithm="
	+ algorithm + ", hashType=" + hashType + ", resultEncoding="
	+ resultEncoding + ", challenge=" + challenge + ", verifier="
	+ verifier + "]";
  }
}
