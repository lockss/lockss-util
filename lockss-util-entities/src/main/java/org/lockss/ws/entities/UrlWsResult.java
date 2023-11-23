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
 * Container for the information related to a URL that is the result of a query.
 */
public class UrlWsResult {
  private String url;
  private Integer versionCount;
  private Long currentVersionSize;
  private Float pollWeight;

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public Integer getVersionCount() {
    return versionCount;
  }
  public void setVersionCount(Integer versionCount) {
    this.versionCount = versionCount;
  }
  public Long getCurrentVersionSize() {
    return currentVersionSize;
  }
  public void setCurrentVersionSize(Long currentVersionSize) {
    this.currentVersionSize = currentVersionSize;
  }
  public Float getPollWeight() {
    return pollWeight;
  }
  public void setPollWeight(Float pollWeight) {
    this.pollWeight = pollWeight;
  }

  @Override
  public String toString() {
    return "[UrlWsResult url=" + url + ", versionCount=" + versionCount
	+ ", currentVersionSize=" + currentVersionSize
	+ ", pollWeight=" + pollWeight + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UrlWsResult that = (UrlWsResult) o;
    return Objects.equals(url, that.url) &&
        Objects.equals(versionCount, that.versionCount) &&
        Objects.equals(currentVersionSize, that.currentVersionSize) &&
        Objects.equals(pollWeight, that.pollWeight);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, versionCount, currentVersionSize, pollWeight);
  }
}
