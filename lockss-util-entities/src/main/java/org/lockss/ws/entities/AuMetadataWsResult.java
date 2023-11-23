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
 * Container for the metadata information of an archival unit.
 */
public class AuMetadataWsResult {
  private String auId;
  private Long auSeq;
  private Long auMdSeq;
  private Integer mdVersion;
  private Long extractTime;
  private Long creationTime;
  private Long providerSeq;
  private String providerName;
  private Integer itemCount;

  public String getAuId() {
    return auId;
  }
  public void setAuId(String auId) {
    this.auId = auId;
  }

  public Long getAuSeq() {
    return auSeq;
  }
  public void setAuSeq(Long auSeq) {
    this.auSeq = auSeq;
  }

  public Long getAuMdSeq() {
    return auMdSeq;
  }
  public void setAuMdSeq(Long auMdSeq) {
    this.auMdSeq = auMdSeq;
  }

  public Integer getMdVersion() {
    return mdVersion;
  }
  public void setMdVersion(Integer mdVersion) {
    this.mdVersion = mdVersion;
  }

  public Long getExtractTime() {
    return extractTime;
  }
  public void setExtractTime(Long extractTime) {
    this.extractTime = extractTime;
  }

  public Long getCreationTime() {
    return creationTime;
  }
  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  public Long getProviderSeq() {
    return providerSeq;
  }
  public void setProviderSeq(Long providerSeq) {
    this.providerSeq = providerSeq;
  }

  public String getProviderName() {
    return providerName;
  }
  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }

  public Integer getItemCount() {
    return itemCount;
  }
  public void setItemCount(Integer itemCount) {
    this.itemCount = itemCount;
  }

  @Override
  public String toString() {
    return "[AuMetadataWsResult auId=" + auId + ", auSeq=" + auSeq
	+ ", auMdSeq=" + auMdSeq + ", mdVersion=" + mdVersion
	+ ", extractTime=" + extractTime + ", creationTime=" + creationTime
	+ ", providerSeq=" + providerSeq + ", providerName=" + providerName
	+ ", itemCount=" + itemCount + "]";
  }
}
