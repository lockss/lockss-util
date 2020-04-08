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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * The result from a request to perform a crawl.
 */
@Validated
public class Crawl   {
  // The descriptor of the crawl.
  @JsonProperty("crawlDesc")
  private CrawlDesc crawlDesc = null;

  // The time, in ISO-8601 format, the crawl was requested.
  @JsonProperty("creationDate")
  private LocalDate creationDate = null;

  // Identifier of the job performing the crawl.
  @JsonProperty("jobId")
  private String jobId = null;

  // The status of the crawl operation.
  @JsonProperty("status")
  private Status status = null;

  // The time, in ISO-8601 format, the crawl began.
  @JsonProperty("startDate")
  private LocalDate startDate = null;

  // The time, in ISO-8601 format, the crawl ended.
  @JsonProperty("endDate")
  private LocalDate endDate = null;

  // A URI which can be used to retrieve the crawl data.
  @JsonProperty("result")
  private String result = null;

  // The reason for any delay in performing the operation.
  @JsonProperty("delayReason")
  private String delayReason = null;

  public Crawl crawlDesc(CrawlDesc crawlDesc) {
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

  public Crawl creationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl was requested.
   * @return creationDate
  **/
  @NotNull
  @Valid
  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public Crawl jobId(String jobId) {
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

  public Crawl status(Status status) {
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

  public Crawl startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl began.
   * @return startDate
  **/
  @Valid
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public Crawl endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * The time, in ISO-8601 format, the crawl ended.
   * @return endDate
  **/
  @Valid
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Crawl result(String result) {
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

  public Crawl delayReason(String delayReason) {
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
    Crawl crawl = (Crawl) o;
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
    sb.append("class Crawl {\n");
    
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
