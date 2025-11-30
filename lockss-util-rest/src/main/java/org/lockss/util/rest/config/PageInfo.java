/*

Copyright (c) 2000-2025 Board of Trustees of Leland Stanford Jr. University,
all rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package org.lockss.util.rest.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Page information for paginated REST responses.
 */
public class PageInfo {
  @JsonProperty("totalCount")
  private Integer totalCount;

  @JsonProperty("itemsInPage")
  private Integer itemsInPage;

  @JsonProperty("continuationToken")
  private String continuationToken;

  @JsonProperty("curLink")
  private String curLink;

  @JsonProperty("nextLink")
  private String nextLink;

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public Integer getItemsInPage() {
    return itemsInPage;
  }

  public void setItemsInPage(Integer itemsInPage) {
    this.itemsInPage = itemsInPage;
  }

  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public String getCurLink() {
    return curLink;
  }

  public void setCurLink(String curLink) {
    this.curLink = curLink;
  }

  public String getNextLink() {
    return nextLink;
  }

  public void setNextLink(String nextLink) {
    this.nextLink = nextLink;
  }

  @Override
  public String toString() {
    return "PageInfo{" +
        "totalCount=" + totalCount +
        ", itemsInPage=" + itemsInPage +
        ", continuationToken='" + continuationToken + '\'' +
        ", curLink='" + curLink + '\'' +
        ", nextLink='" + nextLink + '\'' +
        '}';
  }
}
