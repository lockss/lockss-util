/*

Copyright (c) 2000-2022, Board of Trustees of Leland Stanford Jr. University

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

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * ImportStatus
 */
@Validated


public class ImportStatus   {
  @JsonProperty("warcId")
  private String warcId = null;

  @JsonProperty("offset")
  private Long offset = null;

  @JsonProperty("url")
  private String url = null;

  @JsonProperty("artifactUuid")
  private String artifactUuid = null;

  @JsonProperty("digest")
  private String digest = null;

  @JsonProperty("version")
  private Integer version = null;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    OK("OK"),
    
    ERROR("ERROR"),

    DUPLICATE("DUPLICATE"),

    EXCLUDED("EXCLUDED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("status")
  private StatusEnum status = null;

  @JsonProperty("statusMessage")
  private String statusMessage = null;

  public ImportStatus warcId(String warcId) {
    this.warcId = warcId;
    return this;
  }

  /**
   * Get warcId
   * @return warcId
   **/
  @Schema(description = "")

    public String getWarcId() {
    return warcId;
  }

  public void setWarcId(String warcId) {
    this.warcId = warcId;
  }

  public ImportStatus offset(Long offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Get offset
   * @return offset
   **/
  @Schema(description = "")

    public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public ImportStatus url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
   **/
  @Schema(description = "")

    public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ImportStatus artifactUuid(String artifactUuid) {
    this.artifactUuid = artifactUuid;
    return this;
  }

  /**
   * Get artifactUuid
   * @return artifactUuid
   **/
  @Schema(description = "")

    public String getArtifactUuid() {
    return artifactUuid;
  }

  public void setArtifactUuid(String artifactUuid) {
    this.artifactUuid = artifactUuid;
  }

  public ImportStatus digest(String digest) {
    this.digest = digest;
    return this;
  }

  /**
   * Get digest
   * @return digest
   **/
  @Schema(description = "")

    public String getDigest() {
    return digest;
  }

  public void setDigest(String digest) {
    this.digest = digest;
  }

  public ImportStatus version(Integer version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   **/
  @Schema(description = "")

    public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public ImportStatus status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   **/
  @Schema(description = "")

    public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public ImportStatus statusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

  /**
   * Get statusMessage
   * @return statusMessage
   **/
  @Schema(description = "")

    public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ImportStatus importStatus = (ImportStatus) o;
    return Objects.equals(this.warcId, importStatus.warcId) &&
        Objects.equals(this.offset, importStatus.offset) &&
        Objects.equals(this.url, importStatus.url) &&
        Objects.equals(this.artifactUuid, importStatus.artifactUuid) &&
        Objects.equals(this.digest, importStatus.digest) &&
        Objects.equals(this.version, importStatus.version) &&
        Objects.equals(this.status, importStatus.status) &&
        Objects.equals(this.statusMessage, importStatus.statusMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(warcId, offset, url, artifactUuid, digest, version, status, statusMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ImportStatus {\n");
    
    sb.append("    warcId: ").append(toIndentedString(warcId)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    artifactUuid: ").append(toIndentedString(artifactUuid)).append("\n");
    sb.append("    digest: ").append(toIndentedString(digest)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusMessage: ").append(toIndentedString(statusMessage)).append("\n");
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

