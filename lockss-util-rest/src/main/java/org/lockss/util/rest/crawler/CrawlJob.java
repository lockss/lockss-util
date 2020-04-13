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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

/**
 * The job resulting from a request to perform a crawl.
 */
@Validated
public class CrawlJob   {
  // The descriptor of the crawl.
  @JsonProperty("crawlDesc")
  private CrawlDesc crawlDesc = null;

  // The time, in ISO-8601 format, the crawl was requested.
  @JsonProperty("creationDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime creationDate = null;

  // Identifier of the job performing the crawl.
  @JsonProperty("jobId")
  private String jobId = null;

  // The status of the crawl operation.
  @JsonProperty("status")
  private Status status = null;

  // The time, in ISO-8601 format, the crawl began.
  @JsonProperty("startDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime startDate = null;

  // The time, in ISO-8601 format, the crawl ended.
  @JsonProperty("endDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime endDate = null;

  // A URI which can be used to retrieve the crawl data.
  @JsonProperty("result")
  private String result = null;

  // The reason for any delay in performing the operation.
  @JsonProperty("delayReason")
  private String delayReason = null;

  public CrawlJob crawlDesc(CrawlDesc crawlDesc) {
    this.crawlDesc = crawlDesc;
    return this;
  }

  /**
   * The descriptor of the crawl.
   * @return crawlDesc
  **/
  @NotNull
  @Valid
  public CrawlDesc getCrawlDesc() {
    return crawlDesc;
  }

  public void setCrawlDesc(CrawlDesc crawlDesc) {
    this.crawlDesc = crawlDesc;
  }

  @JsonSetter("creationDate")
  public void setCreationDate(String isoDate) {
    this.creationDate = LocalDateTime.parse(isoDate);
  }

  public CrawlJob creationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl was requested.
   * @return creationDate
  **/
  @NotNull
  @Valid
  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public CrawlJob jobId(String jobId) {
    this.jobId = jobId;
    return this;
  }

  /**
   * Identifier of the job performing the crawl.
   * @return jobId
  **/
  @NotNull
  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public CrawlJob status(Status status) {
    this.status = status;
    return this;
  }

  /**
   *  The status of the crawl operation.
   * @return status
  **/
  @NotNull
  @Valid
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public CrawlJob startDate(LocalDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl began.
   * @return startDate
  **/
  @Valid
  public LocalDateTime getStartDate() {
    return startDate;
  }

  @JsonSetter("startDate")
  public void setStartDate(String isoDate) {
    this.startDate = LocalDateTime.parse(isoDate);
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public CrawlJob endDate(LocalDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl ended.
   * @return endDate
  **/
  @Valid
  public LocalDateTime getEndDate() {
    return endDate;
  }

  @JsonSetter("endDate")
  public void setEndDate(String isoDate) {
    this.endDate = LocalDateTime.parse(isoDate);
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public CrawlJob result(String result) {
    this.result = result;
    return this;
  }

  /**
   * A URI which can be used to retrieve the crawl data.
   * @return result
  **/
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public CrawlJob delayReason(String delayReason) {
    this.delayReason = delayReason;
    return this;
  }

  /**
   * The reason for any delay in performing the operation.
   * @return delayReason
  **/
  public String getDelayReason() {
    return delayReason;
  }

  public void setDelayReason(String delayReason) {
    this.delayReason = delayReason;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CrawlJob crawl = (CrawlJob) o;
    return Objects.equals(this.crawlDesc, crawl.crawlDesc) &&
        Objects.equals(this.creationDate, crawl.creationDate) &&
        Objects.equals(this.jobId, crawl.jobId) &&
        Objects.equals(this.status, crawl.status) &&
        Objects.equals(this.startDate, crawl.startDate) &&
        Objects.equals(this.endDate, crawl.endDate) &&
        Objects.equals(this.result, crawl.result) &&
        Objects.equals(this.delayReason, crawl.delayReason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(crawlDesc, creationDate, jobId, status, startDate,
	endDate, result, delayReason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CrawlJob {\n");
    
    sb.append("    crawlDesc: ").append(toIndentedString(crawlDesc))
    .append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate))
    .append("\n");
    sb.append("    jobId: ").append(toIndentedString(jobId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate))
    .append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    delayReason: ").append(toIndentedString(delayReason))
    .append("\n");
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
