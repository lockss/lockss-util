/*

Copyright (c) 2000-2024, Board of Trustees of Leland Stanford Jr. University

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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * A status which includes a code and a message.
 */
@Schema(description = "A status which includes a code and a message.")
@Validated



public class JobStatus   {
  /**
   * The numeric value for this status.
   */
  public enum StatusCodeEnum {
    UNKNOWN("STATUS_UNKNOWN"),
    
    QUEUED("STATUS_QUEUED"),
    
    ACTIVE("STATUS_ACTIVE"),
    
    SUCCESSFUL("STATUS_SUCCESSFUL"),
    
    ERROR("STATUS_ERROR"),
    
    ABORTED("STATUS_ABORTED"),
    
    WINDOW_CLOSED("STATUS_WINDOW_CLOSED"),
    
    FETCH_ERROR("STATUS_FETCH_ERROR"),
    
    NO_PUB_PERMISSION("STATUS_NO_PUB_PERMISSION"),
    
    PLUGIN_ERROR("STATUS_PLUGIN_ERROR"),
    
    REPO_ERR("STATUS_REPO_ERR"),
    
    RUNNING_AT_CRASH("STATUS_RUNNING_AT_CRASH"),
    
    EXTRACTOR_ERROR("STATUS_EXTRACTOR_ERROR"),
    
    CRAWL_TEST_SUCCESSFUL("STATUS_CRAWL_TEST_SUCCESSFUL"),
    
    CRAWL_TEST_FAIL("STATUS_CRAWL_TEST_FAIL"),
    
    INELIGIBLE("STATUS_INELIGIBLE"),
    
    INACTIVE_REQUEST("STATUS_INACTIVE_REQUEST"),
    
    INTERRUPTED("STATUS_INTERRUPTED");

    private String value;

    StatusCodeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusCodeEnum fromValue(String text) {
      for (StatusCodeEnum b : StatusCodeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("statusCode")
  private StatusCodeEnum statusCode = null;

  @JsonProperty("msg")
  private String msg = null;

  public JobStatus statusCode(StatusCodeEnum statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * The numeric value for this status.
   * @return statusCode
   **/
  @Schema(required = true, description = "The numeric value for this status.")
      @NotNull

    public StatusCodeEnum getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(StatusCodeEnum statusCode) {
    this.statusCode = statusCode;
  }

  public JobStatus msg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * A text message explaining this status.
   * @return msg
   **/
  @Schema(description = "A text message explaining this status.")
  
    public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JobStatus jobStatus = (JobStatus) o;
    return Objects.equals(this.statusCode, jobStatus.statusCode) &&
        Objects.equals(this.msg, jobStatus.msg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusCode, msg);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JobStatus {\n");
    
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
    sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
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
