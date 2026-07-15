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

package org.lockss.util.rest.poller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import org.lockss.util.rest.repo.model.PageInfo;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * A display page of voter poll summaries
 */
@Schema(description = "A display page of voter poll summaries")
@Validated



public class VoterPageInfo   {
  @JsonProperty("polls")
  @Valid
  private List<VoterSummary> polls = new ArrayList<>();

  @JsonProperty("pageInfo")
  private PageInfo pageInfo = null;

  public VoterPageInfo polls(List<VoterSummary> polls) {
    this.polls = polls;
    return this;
  }

  public VoterPageInfo addPollsItem(VoterSummary pollsItem) {
    this.polls.add(pollsItem);
    return this;
  }

  /**
   * The poll summaries included in the page
   * @return polls
   **/
  @Schema(title = "Polls",
          description = "The poll summaries included in the page",
          requiredMode = RequiredMode.REQUIRED,
          nullable = false)
  @NotNull
  @Valid
  public List<VoterSummary> getPolls() {
    return polls;
  }

  /**
   * Saves the poll summaries included in the page.
   * @param polls A List<VoterSummary> with the poll summaries included in the page.
   **/
  public void setPolls(List<VoterSummary> polls) {
    this.polls = polls;
  }

  public VoterPageInfo pageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
    return this;
  }

  /**
   * Information about the page
   * @return pageInfo
   **/
  @Schema(title = "Page Information",
          description = "Information about the page",
          requiredMode = RequiredMode.REQUIRED,
          nullable = false)
  @NotNull
  @Valid
  public PageInfo getPageInfo() {
    return pageInfo;
  }

  /**
   * Saves the pagination information.
   *
   * @param pageInfo A PageInfo with the pagination information.
   */
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
    VoterPageInfo voterPageInfo = (VoterPageInfo) o;
    return Objects.equals(this.polls, voterPageInfo.polls) &&
        Objects.equals(this.pageInfo, voterPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polls, pageInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("VoterPageInfo [\n");

    sb.append("    polls: ").append(toIndentedString(polls)).append("\n");
    sb.append("    pageInfo: ").append(toIndentedString(pageInfo)).append("\n");
    sb.append("]");
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
