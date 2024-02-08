/*

Copyright (c) 2000-2019 Board of Trustees of Leland Stanford Jr. University,
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
package org.lockss.util.rest.repo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * A page of archival unit identifier results.
 */
public class AuidPageInfo   {
  @JsonProperty("auids")
  @Valid
  private List<String> auids = new ArrayList<>();

  @JsonProperty("pageInfo")
  private PageInfo pageInfo = null;

  public AuidPageInfo auids(List<String> auids) {
    this.auids = auids;
    return this;
  }

  public AuidPageInfo addAuidsItem(String auidsItem) {
    this.auids.add(auidsItem);
    return this;
  }

  /**
   * The Archival Unit identifiers included in the page
   * @return auids
   **/
  @Schema(required = true, description = "The Archival Unit identifiers included in the page")
      @NotNull

    public List<String> getAuids() {
    return auids;
  }

  /**
   * Saves the archival unit identifiers included in the page.
   * 
   * @param auids A List<String> with the archival unit identifiers included in
   *              the page.
   */
  public void setAuids(List<String> auids) {
    this.auids = auids;
  }

  public AuidPageInfo pageInfo(PageInfo pageInfo) {
    this.pageInfo = pageInfo;
    return this;
  }

  /**
   * Get pageInfo
   * @return pageInfo
   **/
  @Schema(required = true, description = "")
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
    AuidPageInfo auidPageInfo = (AuidPageInfo) o;
    return Objects.equals(this.auids, auidPageInfo.auids) &&
        Objects.equals(this.pageInfo, auidPageInfo.pageInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auids, pageInfo);
  }

  @Override
  public String toString() {
    return "[AuidPageInfo auids=" + auids + ", pageInfo=" + pageInfo + "]";
  }
}
