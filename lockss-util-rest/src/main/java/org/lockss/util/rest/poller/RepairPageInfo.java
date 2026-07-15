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
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.lockss.util.rest.repo.model.PageInfo;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Schema(description = "A page of repair queue elements.")
@Validated
public class RepairPageInfo {
  @JsonProperty("repairs")
  @Valid
  private List<RepairData> repairs = new ArrayList<>();

  @JsonProperty("pageInfo")
  private PageInfo pageInfo = null;

  public RepairPageInfo repairs(List<RepairData> repairs) {
    this.repairs = repairs;
    return this;
  }

  public RepairPageInfo addRepairsItem(RepairData repairsItem) {
    this.repairs.add(repairsItem);
    return this;
  }

  @Schema(required = true, description = "The repair items included in the page")
  @NotNull
  @Valid
  public List<RepairData> getRepairs() {
    return repairs;
  }

  public void setRepairs(List<RepairData> repairs) {
    this.repairs = repairs;
  }

  public RepairPageInfo pageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
    return this;
  }

  @Schema(required = true, description = "")
  @NotNull
  @Valid
  public PageInfo getPageInfo() {
    return pageInfo;
  }

  public void setPageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RepairPageInfo repairPageInfo = (RepairPageInfo) o;
    return Objects.equals(this.repairs, repairPageInfo.repairs) &&
        Objects.equals(this.pageInfo, repairPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(repairs, pageInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RepairPageInfo {\n");
    sb.append("    repairs: ").append(toIndentedString(repairs)).append("\n");
    sb.append("    pageInfo: ").append(toIndentedString(pageInfo)).append("\n");
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
