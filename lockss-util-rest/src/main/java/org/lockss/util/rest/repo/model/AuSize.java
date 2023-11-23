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

package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * AuSize
 */
@Validated


public class AuSize   {
  @JsonProperty("totalLatestVersions")
  private long totalLatestVersions = 0L;

  @JsonProperty("totalAllVersions")
  private long totalAllVersions = 0L;

  @JsonProperty("totalWarcSize")
  private long totalWarcSize = 0L;

  public AuSize totalLatestVersions(Long totalLatestVersions) {
    this.totalLatestVersions = totalLatestVersions;
    return this;
  }

  /**
   * Get totalLatestVersions
   * @return totalLatestVersions
  **/
  @ApiModelProperty(value = "")


  public Long getTotalLatestVersions() {
    return totalLatestVersions;
  }

  public void setTotalLatestVersions(Long totalLatestVersions) {
    this.totalLatestVersions = totalLatestVersions;
  }

  public AuSize totalAllVersions(Long totalAllVersions) {
    this.totalAllVersions = totalAllVersions;
    return this;
  }

  /**
   * Get totalAllVersions
   * @return totalAllVersions
  **/
  @ApiModelProperty(value = "")


  public Long getTotalAllVersions() {
    return totalAllVersions;
  }

  public void setTotalAllVersions(Long totalAllVersions) {
    this.totalAllVersions = totalAllVersions;
  }

  public AuSize totalWarcSize(Long totalWarcSize) {
    this.totalWarcSize = totalWarcSize;
    return this;
  }

  /**
   * Get totalWarcSize
   * @return totalWarcSize
  **/
  @ApiModelProperty(value = "")


  public Long getTotalWarcSize() {
    return totalWarcSize;
  }

  public void setTotalWarcSize(Long totalWarcSize) {
    this.totalWarcSize = totalWarcSize;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuSize auSize = (AuSize) o;
    return Objects.equals(this.totalLatestVersions, auSize.totalLatestVersions) &&
        Objects.equals(this.totalAllVersions, auSize.totalAllVersions) &&
        Objects.equals(this.totalWarcSize, auSize.totalWarcSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalLatestVersions, totalAllVersions, totalWarcSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuSize {\n");
    
    sb.append("    totalLatestVersions: ").append(toIndentedString(totalLatestVersions)).append("\n");
    sb.append("    totalAllVersions: ").append(toIndentedString(totalAllVersions)).append("\n");
    sb.append("    totalWarcSize: ").append(toIndentedString(totalWarcSize)).append("\n");
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

