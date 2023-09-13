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
package org.lockss.util.rest.poller;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * A set of urls bounded by  upper and lower restraints.  If lower &#x3D; \&quot;.\&quot; this is a singleContentNode. If lower is null then start at the root url, if upper is null end with the last.
 */
@ApiModel(description = "A set of urls bounded by  upper and lower restraints.  If lower = \".\" this is a singleContentNode. If lower is null then start at the root url, if upper is null end with the last.")
@Validated

public class CachedUriSetSpec   {
  @JsonProperty("urlPrefix")
  private String urlPrefix = null;

  @JsonProperty("lowerBound")
  private String lowerBound = null;

  @JsonProperty("upperBound")
  private String upperBound = null;

  public CachedUriSetSpec urlPrefix(String urlPrefix) {
    this.urlPrefix = urlPrefix;
    return this;
  }

  /**
   * The base which roots the lower and upper bound
   * @return urlPrefix
  **/
  @ApiModelProperty(required = true, value = "The base which roots the lower and upper bound")
  @NotNull


  public String getUrlPrefix() {
    return urlPrefix;
  }

  public void setUrlPrefix(String urlPrefix) {
    this.urlPrefix = urlPrefix;
  }

  public CachedUriSetSpec lowerBound(String lowerBound) {
    this.lowerBound = lowerBound;
    return this;
  }

  /**
   * lower bound of the prefix range, inclusive.
   * @return lowerBound
  **/
  @ApiModelProperty(value = "lower bound of the prefix range, inclusive.")


  public String getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(String lowerBound) {
    this.lowerBound = lowerBound;
  }

  public CachedUriSetSpec upperBound(String upperBound) {
    this.upperBound = upperBound;
    return this;
  }

  /**
   * upper bound of prefix range, inclusive.
   * @return upperBound
  **/
  @ApiModelProperty(value = "upper bound of prefix range, inclusive.")


  public String getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(String upperBound) {
    this.upperBound = upperBound;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CachedUriSetSpec cachedUriSetSpec = (CachedUriSetSpec) o;
    return Objects.equals(this.urlPrefix, cachedUriSetSpec.urlPrefix) &&
        Objects.equals(this.lowerBound, cachedUriSetSpec.lowerBound) &&
        Objects.equals(this.upperBound, cachedUriSetSpec.upperBound);
  }

  @Override
  public int hashCode() {
    return Objects.hash(urlPrefix, lowerBound, upperBound);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CachedUriSetSpec {\n");
    
    sb.append("    urlPrefix: ").append(toIndentedString(urlPrefix)).append("\n");
    sb.append("    lowerBound: ").append(toIndentedString(lowerBound)).append("\n");
    sb.append("    upperBound: ").append(toIndentedString(upperBound)).append("\n");
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

