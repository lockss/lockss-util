/*

Copyright (c) 2020 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest.crawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * A descriptor for a crawl.
 */
@Validated
public class CrawlDesc   {
  // The identifier of the archival unit to be crawled.
  @JsonProperty("auId")
  private String auId = null;

  // The kind of crawl being performed. For now, this is either 'newContent' or
  // 'repair'.
  @JsonProperty("crawlKind")
  private CrawlKind crawlKind = null;

  // The crawler to be used for this crawl.
  @JsonProperty("crawler")
  private String crawler = "lockss";

  // The repair URLs in a repair crawl.
  @JsonProperty("repairList")
  @Valid
  private List<String> repairList = null;

  // An indication of whether the crawl is to be forced, suppressing conditions
  // that might otherwise prevent the crawl from happening.
  @JsonProperty("forceCrawl")
  private Boolean forceCrawl = false;

  // The refetch depth to use for a deep crawl.
  @JsonProperty("refetchDepth")
  private Integer refetchDepth = -1;

  // The priority for the crawl.
  @JsonProperty("priority")
  private Integer priority = null;

  // The list of URLs to crawl.
  @JsonProperty("crawlList")
  @Valid
  private List<String> crawlList = null;

  // The depth of the crawl.
  @JsonProperty("crawlDepth")
  private Integer crawlDepth = null;

  // A JSON object with content required by the crawler.
  @JsonProperty("extraCrawlerData")
  @Valid
  private String extraCrawlerData = null;

  public CrawlDesc auId(String auId) {
    this.auId = auId;
    return this;
  }

  /**
   * The identifier of the archival unit to be crawled.
   * @return auId
  **/
  @NotNull
  public String getAuId() {
    return auId;
  }

  public void setAuId(String auId) {
    this.auId = auId;
  }

  public CrawlDesc crawlKind(CrawlKind crawlKind) {
    this.crawlKind = crawlKind;
    return this;
  }

  /**
   * The kind of crawl being performed. For now, this is either 'newContent' or
   * 'repair'.
   * @return crawlKind
  **/
  @NotNull
  public CrawlKind getCrawlKind() {
    return crawlKind;
  }

  public void setCrawlKind(CrawlKind crawlKind) {
    this.crawlKind = crawlKind;
  }

  public CrawlDesc crawler(String crawler) {
    this.crawler = crawler;
    return this;
  }

  /**
   * The crawler to be used for this crawl.
   * @return crawler
  **/
  public String getCrawler() {
    return crawler;
  }

  public void setCrawler(String crawler) {
    this.crawler = crawler;
  }

  public CrawlDesc repairList(List<String> repairList) {
    this.repairList = repairList;
    return this;
  }

  public CrawlDesc addRepairListItem(String repairListItem) {
    if (this.repairList == null) {
      this.repairList = new ArrayList<>();
    }
    this.repairList.add(repairListItem);
    return this;
  }

  /**
   * The repair URLs in a repair crawl.
   * @return repairList
  **/
  public List<String> getRepairList() {
    return repairList;
  }

  public void setRepairList(List<String> repairList) {
    this.repairList = repairList;
  }

  public CrawlDesc forceCrawl(Boolean forceCrawl) {
    this.forceCrawl = forceCrawl;
    return this;
  }

  /**
   * An indication of whether the crawl is to be forced, suppressing conditions
   * that might otherwise prevent the crawl from happening.
   * @return forceCrawl
  **/
  public Boolean isForceCrawl() {
    return forceCrawl;
  }

  public void setForceCrawl(Boolean forceCrawl) {
    this.forceCrawl = forceCrawl;
  }

  public CrawlDesc refetchDepth(Integer refetchDepth) {
    this.refetchDepth = refetchDepth;
    return this;
  }

  /**
   * The refetch depth to use for a deep crawl.
   * @return refetchDepth
  **/
  public Integer getRefetchDepth() {
    return refetchDepth;
  }

  public void setRefetchDepth(Integer refetchDepth) {
    this.refetchDepth = refetchDepth;
  }

  public CrawlDesc priority(Integer priority) {
    this.priority = priority;
    return this;
  }

  /**
   * The priority for the crawl.
   * @return priority
  **/
  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public CrawlDesc crawlList(List<String> crawlList) {
    this.crawlList = crawlList;
    return this;
  }

  public CrawlDesc addCrawlListItem(String crawlListItem) {
    if (this.crawlList == null) {
      this.crawlList = new ArrayList<>();
    }
    this.crawlList.add(crawlListItem);
    return this;
  }

  /**
   * The list of URLs to crawl.
   * @return crawlList
  **/
  public List<String> getCrawlList() {
    return crawlList;
  }

  public void setCrawlList(List<String> crawlList) {
    this.crawlList = crawlList;
  }

  public CrawlDesc crawlDepth(Integer crawlDepth) {
    this.crawlDepth = crawlDepth;
    return this;
  }

  /**
   * The depth of the crawl.
   * @return crawlDepth
  **/
  public Integer getCrawlDepth() {
    return crawlDepth;
  }

  public void setCrawlDepth(Integer crawlDepth) {
    this.crawlDepth = crawlDepth;
  }

  public CrawlDesc extraCrawlerData(String extraCrawlerData) {
    this.extraCrawlerData = extraCrawlerData;
    return this;
  }

  /**
   * A JSON object with content required by the crawler.
   * @return extraCrawlerData
  **/
  public String getExtraCrawlerData() {
    return extraCrawlerData;
  }

  public void setExtraCrawlerData(String extraCrawlerData) {
    this.extraCrawlerData = extraCrawlerData;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CrawlDesc crawlDesc = (CrawlDesc) o;
    return Objects.equals(this.auId, crawlDesc.auId) &&
        Objects.equals(this.crawlKind, crawlDesc.crawlKind) &&
        Objects.equals(this.crawler, crawlDesc.crawler) &&
        Objects.equals(this.repairList, crawlDesc.repairList) &&
        Objects.equals(this.forceCrawl, crawlDesc.forceCrawl) &&
        Objects.equals(this.refetchDepth, crawlDesc.refetchDepth) &&
        Objects.equals(this.priority, crawlDesc.priority) &&
        Objects.equals(this.crawlList, crawlDesc.crawlList) &&
        Objects.equals(this.crawlDepth, crawlDesc.crawlDepth) &&
        Objects.equals(this.extraCrawlerData, crawlDesc.extraCrawlerData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, crawlKind, crawler, repairList, forceCrawl,
	refetchDepth, priority, crawlList, crawlDepth, extraCrawlerData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CrawlDesc {\n");
    
    sb.append("    auId: ").append(toIndentedString(auId)).append("\n");
    sb.append("    crawlKind: ").append(toIndentedString(crawlKind))
    .append("\n");
    sb.append("    crawler: ").append(toIndentedString(crawler)).append("\n");
    sb.append("    repairList: ").append(toIndentedString(repairList))
    .append("\n");
    sb.append("    forceCrawl: ").append(toIndentedString(forceCrawl))
    .append("\n");
    sb.append("    refetchDepth: ").append(toIndentedString(refetchDepth))
    .append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    crawlList: ").append(toIndentedString(crawlList))
    .append("\n");
    sb.append("    crawlDepth: ").append(toIndentedString(crawlDepth))
    .append("\n");
    sb.append("    extraCrawlerData: ")
    .append(toIndentedString(extraCrawlerData)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
