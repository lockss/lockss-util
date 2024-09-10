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
package org.lockss.util.rest.mdx;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.*;

/**
 * The information defining an AU metadata update operation
 */
@Schema(description = "The information defining an AU metadata update operation")
@Validated



public class MetadataUpdateSpec   {
  @JsonProperty("auid")
  private String auid = null;

  @JsonProperty("updateType")
  private String updateType = null;

  public MetadataUpdateSpec auid(String auid) {
    this.auid = auid;
    return this;
  }

  /**
   * The identifier of the AU for which the metadata update is to be performed
   * @return auid
   **/
  @Schema(required = true, description = "The identifier of the AU for which the metadata update is to be performed")
      @NotNull

    public String getAuid() {
    return auid;
  }

  public void setAuid(String auid) {
    this.auid = auid;
  }

  public MetadataUpdateSpec updateType(String updateType) {
    this.updateType = updateType;
    return this;
  }

  /**
   * The type of metadata update to be performed
   * @return updateType
   **/
  @Schema(example = "full_extraction | incremental_extraction | delete", required = true, description = "The type of metadata update to be performed")
      @NotNull

    public String getUpdateType() {
    return updateType;
  }

  public void setUpdateType(String updateType) {
    this.updateType = updateType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataUpdateSpec metadataUpdateSpec = (MetadataUpdateSpec) o;
    return Objects.equals(this.auid, metadataUpdateSpec.auid) &&
      Objects.equals(this.updateType, metadataUpdateSpec.updateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auid, updateType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetadataUpdateSpec {\n");

    sb.append("    auid: ").append(toIndentedString(auid)).append("\n");
    sb.append("    updateType: ").append(toIndentedString(updateType)).append("\n");
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
