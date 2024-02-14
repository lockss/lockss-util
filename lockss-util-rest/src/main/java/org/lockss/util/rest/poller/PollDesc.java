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
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.lockss.laaws.poller.model.CachedUriSetSpec;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The Poller Services poll spec used to define a poll.
 */
@Schema(description = "The Poller Services poll spec used to define a poll.")
@Validated

public class PollDesc   {
  @JsonProperty("auId")
  private String auId = null;

  @JsonProperty("cuSetSpec")
  private CachedUriSetSpec cuSetSpec = null;

  @JsonProperty("pollType")
  private Integer pollType = null;

  @JsonProperty("protocol")
  private Integer protocol = null;

  @JsonProperty("pluginPollVersion")
  private String pluginPollVersion = null;

  /**
   * The V3 poll variation.
   */
  public enum VariantEnum {
    POR("PoR"),

    POP("PoP"),

    LOCAL("Local"),

    NOPOLL("NoPoll");

    private String value;

    VariantEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static VariantEnum fromValue(String text) {
      for (VariantEnum b : VariantEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("variant")
  private VariantEnum variant = null;

  @JsonProperty("modulus")
  private Integer modulus = null;

  public PollDesc auId(String auId) {
    this.auId = auId;
    return this;
  }

  /**
   * The id which defines the poll
   * @return auId
  **/
  @Schema(required = true, description = "The id which defines the poll")
  @NotNull


  public String getAuId() {
    return auId;
  }

  public void setAuId(String auId) {
    this.auId = auId;
  }

  public PollDesc cuSetSpec(CachedUriSetSpec cuSetSpec) {
    this.cuSetSpec = cuSetSpec;
    return this;
  }

  /**
   * Get cuSetSpec
   * @return cuSetSpec
  **/
  @Schema(description = "")

  @Valid

  public CachedUriSetSpec getCuSetSpec() {
    return cuSetSpec;
  }

  public void setCuSetSpec(CachedUriSetSpec cuSetSpec) {
    this.cuSetSpec = cuSetSpec;
  }

  public PollDesc pollType(Integer pollType) {
    this.pollType = pollType;
    return this;
  }

  /**
   * The type of poll to run. Only V3 is supported.
   * minimum: 3
   * @return pollType
  **/
  @Schema(description = "The type of poll to run. Only V3 is supported.")

  @Min(3)  public Integer getPollType() {
    return pollType;
  }

  public void setPollType(Integer pollType) {
    this.pollType = pollType;
  }

  public PollDesc protocol(Integer protocol) {
    this.protocol = protocol;
    return this;
  }

  /**
   * The version of polling protocol.
   * @return protocol
  **/
  @Schema(description = "The version of polling protocol.")


  public Integer getProtocol() {
    return protocol;
  }

  public void setProtocol(Integer protocol) {
    this.protocol = protocol;
  }

  public PollDesc pluginPollVersion(String pluginPollVersion) {
    this.pluginPollVersion = pluginPollVersion;
    return this;
  }

  /**
   * The version of the polling features needed by the plugin.
   * @return pluginPollVersion
  **/
  @Schema(description = "The version of the polling features needed by the plugin.")


  public String getPluginPollVersion() {
    return pluginPollVersion;
  }

  public void setPluginPollVersion(String pluginPollVersion) {
    this.pluginPollVersion = pluginPollVersion;
  }

  public PollDesc variant(VariantEnum variant) {
    this.variant = variant;
    return this;
  }

  /**
   * The V3 poll variation.
   * @return variant
  **/
  @Schema(description = "The V3 poll variation.")


  public VariantEnum getVariant() {
    return variant;
  }

  public void setVariant(VariantEnum variant) {
    this.variant = variant;
  }

  public PollDesc modulus(Integer modulus) {
    this.modulus = modulus;
    return this;
  }

  /**
   * Poll on every 'n'th url.
   * @return modulus
  **/
  @Schema(description = "Poll on every 'n'th url.")

  public Integer getModulus() {
    return modulus;
  }

  public void setModulus(Integer modulus) {
    this.modulus = modulus;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PollDesc pollDesc = (PollDesc) o;
    return Objects.equals(this.auId, pollDesc.auId) &&
        Objects.equals(this.cuSetSpec, pollDesc.cuSetSpec) &&
        Objects.equals(this.pollType, pollDesc.pollType) &&
        Objects.equals(this.protocol, pollDesc.protocol) &&
        Objects.equals(this.pluginPollVersion, pollDesc.pluginPollVersion) &&
        Objects.equals(this.variant, pollDesc.variant) &&
        Objects.equals(this.modulus, pollDesc.modulus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(auId, cuSetSpec, pollType, protocol, pluginPollVersion, variant, modulus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PollDesc {\n");

    sb.append("    auId: ").append(toIndentedString(auId)).append("\n");
    sb.append("    cuSetSpec: ").append(toIndentedString(cuSetSpec)).append("\n");
    sb.append("    pollType: ").append(toIndentedString(pollType)).append("\n");
    sb.append("    protocol: ").append(toIndentedString(protocol)).append("\n");
    sb.append("    pluginPollVersion: ").append(toIndentedString(pluginPollVersion)).append("\n");
    sb.append("    variant: ").append(toIndentedString(variant)).append("\n");
    sb.append("    modulus: ").append(toIndentedString(modulus)).append("\n");
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
