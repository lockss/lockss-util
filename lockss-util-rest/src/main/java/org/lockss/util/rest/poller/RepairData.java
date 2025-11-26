/*
 * Copyright (c) 2025 Board of Trustees of Leland Stanford Jr. University,
 * all rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Stanford University shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization from Stanford University.
 */
package org.lockss.util.rest.poller;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.*;

@Schema(description = "structure used to define a repair source for url. if the source is null than repair from publisher")
@Validated
public class RepairData {
  @JsonProperty("repairUrl")
  private String repairUrl = null;

  @JsonProperty("repairFrom")
  private String repairFrom = null;

  public enum ResultEnum {
    NOQUORUM("NoQuorum"),
    TOOCLOSE("TooClose"),
    LOST("Lost"),
    LOSTPOLLERONLY("LostPollerOnly"),
    LOSTVOTERONLY("LostVoterOnly"),
    WON("Won");

    private String value;

    ResultEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ResultEnum fromValue(String text) {
      for (ResultEnum b : ResultEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("result")
  private ResultEnum result = null;

  public RepairData repairUrl(String repairUrl) {
    this.repairUrl = repairUrl;
    return this;
  }

  @Schema(required = true, description = "The url to repair")
  @NotNull
  public String getRepairUrl() {
    return repairUrl;
  }

  public void setRepairUrl(String repairUrl) {
    this.repairUrl = repairUrl;
  }

  public RepairData repairFrom(String repairFrom) {
    this.repairFrom = repairFrom;
    return this;
  }

  @Schema(required = true, description = "The peer to repair from")
  @NotNull
  public String getRepairFrom() {
    return repairFrom;
  }

  public void setRepairFrom(String repairFrom) {
    this.repairFrom = repairFrom;
  }

  public RepairData result(ResultEnum result) {
    this.result = result;
    return this;
  }

  @Schema(description = "The status of this repair")
  public ResultEnum getResult() {
    return result;
  }

  public void setResult(ResultEnum result) {
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
    RepairData repairData = (RepairData) o;
    return Objects.equals(this.repairUrl, repairData.repairUrl) &&
        Objects.equals(this.repairFrom, repairData.repairFrom) &&
        Objects.equals(this.result, repairData.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repairUrl, repairFrom, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RepairData {\n");
    sb.append("    repairUrl: ").append(toIndentedString(repairUrl)).append("\n");
    sb.append("    repairFrom: ").append(toIndentedString(repairFrom)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
