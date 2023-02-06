package org.lockss.util.rest.crawler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import java.util.Objects;

import javax.validation.constraints.*;

/**
 * A status which includes a code and a message.
 */
@ApiModel(description = "A status which includes a code and a message.")
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
  @ApiModelProperty(required = true, value = "The numeric value for this status.")
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
  @ApiModelProperty(value = "A text message explaining this status.")


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

