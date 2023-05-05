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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A descriptor for a crawl.
 */
@ApiModel(description = "A descriptor for a crawl.")
@Validated


public class CrawlDesc   {
  /**
   * The identifier of the classic LOCKSS crawler.
   */
  public static final String CLASSIC_CRAWLER_ID = "classic";


  // The identifier of the archival unit to be crawled.
  @JsonProperty("auId")
  private String auId = null;

  /**
   * The kind of crawl being performed either 'newContent' or 'repair'.
   */
  public enum CrawlKindEnum {
    NEWCONTENT("newContent"),

    REPAIR("repair");

    private String value;

    CrawlKindEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CrawlKindEnum fromValue(String text) {
      for (CrawlKindEnum b : CrawlKindEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("crawlKind")
  private CrawlKindEnum crawlKind = null;

  @JsonProperty("crawlerId")
  private String crawlerId = "classic";

  @JsonProperty("forceCrawl")
  private Boolean forceCrawl = false;

  @JsonProperty("refetchDepth")
  private Integer refetchDepth = -1;

  @JsonProperty("priority")
  private Integer priority = 0;

  @JsonProperty("crawlList")
  @Valid
  private List<String> crawlList = null;

  @JsonProperty("crawlDepth")
  private Integer crawlDepth = 0;

  @JsonProperty("extraCrawlerData")
  @Valid
  private Map<String, Object> extraCrawlerData = null;

  public CrawlDesc auId(String auId) {
    this.auId = auId;
    return this;
  }

  /**
   * The identifier of the archival unit to be crawled.
   * @return auId
   **/
  @ApiModelProperty(required = true, value = "The identifier of the archival unit to be crawled.")
  @NotNull


  public String getAuId() {
    return auId;
  }

  public void setAuId(String auId) {
    this.auId = auId;
  }

  public CrawlDesc crawlKind(CrawlKindEnum crawlKind) {
    this.crawlKind = crawlKind;
    return this;
  }

  /**
   * The kind of crawl being performed either 'newContent' or 'repair'.
   * @return crawlKind
  **/
  @ApiModelProperty(required = true, value = "The kind of crawl being performed either 'newContent' or 'repair'.")
  @NotNull


  public CrawlKindEnum getCrawlKind() {
    return crawlKind;
  }

  public void setCrawlKind(CrawlKindEnum crawlKind) {
    this.crawlKind = crawlKind;
  }

  public CrawlDesc crawlerId(String crawlerId) {
    this.crawlerId = crawlerId;
    return this;
  }

  /**
   * The crawler to be used for this crawl.
   * @return crawlerId
   **/
  @ApiModelProperty(value = "The crawler to be used for this crawl.")


  public String getCrawlerId() {
    return crawlerId;
  }

  public void setCrawlerId(String crawlerId) {
    this.crawlerId = crawlerId;
  }

  public CrawlDesc forceCrawl(Boolean forceCrawl) {
    this.forceCrawl = forceCrawl;
    return this;
  }

  /**
   * An indication of whether the crawl is to be forced,\\ \\ suppressing conditions that might otherwise prevent the crawl from\\ \\ happening.
   * @return forceCrawl
   **/
  @ApiModelProperty(value = "An indication of whether the crawl is to be forced,\\ \\ suppressing conditions that might otherwise prevent the crawl from\\ \\ happening.")


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
  @ApiModelProperty(value = "The refetch depth to use for a deep crawl.")


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
  @ApiModelProperty(value = "The priority for the crawl.")


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
  @ApiModelProperty(value = "The list of URLs to crawl.")


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
   * The depth to which the links should be followed. 0 means\\ \\ do not follow links.
   * @return crawlDepth
   **/
  @ApiModelProperty(value = "The depth to which the links should be followed. 0 means\\ \\ do not follow links.")


  public Integer getCrawlDepth() {
    return crawlDepth;
  }

  public void setCrawlDepth(Integer crawlDepth) {
    this.crawlDepth = crawlDepth;
  }

  public CrawlDesc extraCrawlerData(Map<String, Object> extraCrawlerData) {
    this.extraCrawlerData = extraCrawlerData;
    return this;
  }

  public CrawlDesc putExtraCrawlerDataItem(String key, Object extraCrawlerDataItem) {
    if (this.extraCrawlerData == null) {
      this.extraCrawlerData = new HashMap<>();
    }
    this.extraCrawlerData.put(key, extraCrawlerDataItem);
    return this;
  }

  /**
   * A map of additional properties for a crawl on a given crawler.
   * @return extraCrawlerData
   **/
  @ApiModelProperty(value = "A map of additional properties for a crawl on a given crawler.")


  public Map<String, Object> getExtraCrawlerData() {
    return extraCrawlerData;
  }

  public void setExtraCrawlerData(Map<String, Object> extraCrawlerData) {
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
        Objects.equals(this.crawlerId, crawlDesc.crawlerId) &&
        Objects.equals(this.forceCrawl, crawlDesc.forceCrawl) &&
        Objects.equals(this.refetchDepth, crawlDesc.refetchDepth) &&
        Objects.equals(this.priority, crawlDesc.priority) &&
        Objects.equals(this.crawlList, crawlDesc.crawlList) &&
        Objects.equals(this.crawlDepth, crawlDesc.crawlDepth) &&
        Objects.equals(this.extraCrawlerData, crawlDesc.extraCrawlerData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, crawlKind, crawlerId, forceCrawl, refetchDepth, priority, crawlList, crawlDepth, extraCrawlerData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CrawlDesc {\n");

    sb.append("    auId: ").append(toIndentedString(auId)).append("\n");
    sb.append("    crawlKind: ").append(toIndentedString(crawlKind)).append("\n");
    sb.append("    crawlerId: ").append(toIndentedString(crawlerId)).append("\n");
    sb.append("    forceCrawl: ").append(toIndentedString(forceCrawl)).append("\n");
    sb.append("    refetchDepth: ").append(toIndentedString(refetchDepth)).append("\n");
    sb.append("    priority: ").append(toIndentedString(priority)).append("\n");
    sb.append("    crawlList: ").append(toIndentedString(crawlList)).append("\n");
    sb.append("    crawlDepth: ").append(toIndentedString(crawlDepth)).append("\n");
    sb.append("    extraCrawlerData: ").append(toIndentedString(extraCrawlerData)).append("\n");
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
