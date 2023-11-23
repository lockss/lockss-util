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

package org.lockss.util.rest.crawler;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The job resulting from a request to perform a crawl.
 */
@ApiModel(description = "The job resulting from a request to perform a crawl.")
@Validated


public class CrawlJob   {
  // The descriptor of the crawl.
  @JsonProperty("crawlDesc")
  private CrawlDesc crawlDesc = null;
  // The time the crawl was requested.

  @JsonProperty("requestDate")
  private Long requestDate = null;

  // Identifier of the job performing the crawl.
  @JsonProperty("jobId")
  private String jobId = null;

  // The status of the crawl operation.
  @JsonProperty("jobStatus")
  private JobStatus jobStatus = null;
  // The time the crawl began.
  @JsonProperty("startDate")
  private Long startDate = null;

  // The time the crawl ended.
  @JsonProperty("endDate")
  private Long endDate = null;

  // A URI which can be used to retrieve the crawl data.
  @JsonProperty("result")
  private String result = null;

  public CrawlJob crawlDesc(CrawlDesc crawlDesc) {
    this.crawlDesc = crawlDesc;
    return this;
  }

  /**
   * The descriptor of the crawl.
   * @return crawlDesc
   **/
  @ApiModelProperty(required = true, value = "The descriptor of the crawl.")
  @NotNull

  @Valid

  public CrawlDesc getCrawlDesc() {
    return crawlDesc;
  }

  public void setCrawlDesc(CrawlDesc crawlDesc) {
    this.crawlDesc = crawlDesc;
  }

  public CrawlJob requestDate(Long requestDate) {
    this.requestDate = requestDate;
    return this;
  }

  /**
   * The timestamp when the crawl was requested.
   * @return requestDate
   **/
  @ApiModelProperty(required = true, value = "The timestamp when the crawl was requested.")
  @NotNull


  public Long getRequestDate() {
    return requestDate;
  }

  public void setRequestDate(Long requestDate) {
    this.requestDate = requestDate;
  }

  public CrawlJob jobId(String jobId) {
    this.jobId = jobId;
    return this;
  }

  /**
   * Identifier of the crawl job.
   * @return jobId
   **/
  @ApiModelProperty(required = true, value = "Identifier of the crawl job.")
  @NotNull


  public String getJobId() {
    return jobId;
  }

  public void setJobId(String jobId) {
    this.jobId = jobId;
  }

  public CrawlJob jobStatus(JobStatus jobStatus) {
    this.jobStatus = jobStatus;
    return this;
  }

  /**
   * The status of the crawl operation.
   * @return jobStatus
   **/
  @ApiModelProperty(required = true, value = "The status of the crawl operation.")
  @NotNull

  @Valid

  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(JobStatus jobStatus) {
    this.jobStatus = jobStatus;
  }

  public CrawlJob startDate(Long startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The timestamp when the crawl began.
   * @return startDate
   **/
  @ApiModelProperty(value = "The timestamp when the crawl began.")


  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public CrawlJob endDate(Long endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * The timestamp when the crawl ended.
   * @return endDate
   **/
  @ApiModelProperty(value = "The timestamp when the crawl ended.")


  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
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
  @ApiModelProperty(value = "A URI which can be used to retrieve the crawl data.")


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CrawlJob crawlJob = (CrawlJob) o;
    return Objects.equals(this.crawlDesc, crawlJob.crawlDesc) &&
      Objects.equals(this.requestDate, crawlJob.requestDate) &&
      Objects.equals(this.jobId, crawlJob.jobId) &&
      Objects.equals(this.jobStatus, crawlJob.jobStatus) &&
      Objects.equals(this.startDate, crawlJob.startDate) &&
      Objects.equals(this.endDate, crawlJob.endDate) &&
      Objects.equals(this.result, crawlJob.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(crawlDesc, requestDate, jobId, jobStatus, startDate, endDate, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CrawlJob {\n");

    sb.append("    crawlDesc: ").append(toIndentedString(crawlDesc)).append("\n");
    sb.append("    requestDate: ").append(toIndentedString(requestDate)).append("\n");
    sb.append("    jobId: ").append(toIndentedString(jobId)).append("\n");
    sb.append("    jobStatus: ").append(toIndentedString(jobStatus)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

