package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * ArtifactProperties
 */
@Validated



public class ArtifactProperties   {
  @JsonProperty("namespace")
  private String namespace = "lockss";

  @JsonProperty("uuid")
  private String uuid = null;

  @JsonProperty("auid")
  private String auid = null;

  @JsonProperty("uri")
  private String uri = null;

  @JsonProperty("version")
  private Integer version = null;

  @JsonProperty("contentLength")
  private Long contentLength = null;

  @JsonProperty("contentDigest")
  private String contentDigest = null;

  @JsonProperty("collectionDate")
  private Long collectionDate = null;

  @JsonProperty("storeDate")
  private Long storeDate = null;

  @JsonProperty("state")
  private String state = null;

  public ArtifactProperties namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  /**
   * Get namespace
   * @return namespace
   **/
  @Schema(description = "")

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public ArtifactProperties uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * Get uuid
   * @return uuid
   **/
  @Schema(description = "")

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public ArtifactProperties auid(String auid) {
    this.auid = auid;
    return this;
  }

  /**
   * Get auid
   * @return auid
   **/
  @Schema(description = "")

  public String getAuid() {
    return auid;
  }

  public void setAuid(String auid) {
    this.auid = auid;
  }

  public ArtifactProperties uri(String uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
   **/
  @Schema(description = "")

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ArtifactProperties version(Integer version) {
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

  public ArtifactProperties contentLength(Long contentLength) {
    this.contentLength = contentLength;
    return this;
  }

  /**
   * Get contentLength
   * @return contentLength
   **/
  @Schema(description = "")

  public Long getContentLength() {
    return contentLength;
  }

  public void setContentLength(Long contentLength) {
    this.contentLength = contentLength;
  }

  public ArtifactProperties contentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
    return this;
  }

  /**
   * Get contentDigest
   * @return contentDigest
   **/
  @Schema(description = "")

  public String getContentDigest() {
    return contentDigest;
  }

  public void setContentDigest(String contentDigest) {
    this.contentDigest = contentDigest;
  }

  public ArtifactProperties collectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
    return this;
  }

  /**
   * Get collectionDate
   * @return collectionDate
   **/
  @Schema(description = "")

  public Long getCollectionDate() {
    return collectionDate;
  }

  public void setCollectionDate(Long collectionDate) {
    this.collectionDate = collectionDate;
  }

  public ArtifactProperties storeDate(Long storeDate) {
    this.storeDate = storeDate;
    return this;
  }

  /**
   * Get storeDate
   * @return storeDate
   **/
  @Schema(description = "")

  public Long getStoreDate() {
    return storeDate;
  }

  public void setStoreDate(Long storeDate) {
    this.storeDate = storeDate;
  }

  public ArtifactProperties state(String state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
   **/
  @Schema(description = "")

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArtifactProperties artifactProperties = (ArtifactProperties) o;
    return Objects.equals(this.namespace, artifactProperties.namespace) &&
      Objects.equals(this.uuid, artifactProperties.uuid) &&
      Objects.equals(this.auid, artifactProperties.auid) &&
      Objects.equals(this.uri, artifactProperties.uri) &&
      Objects.equals(this.version, artifactProperties.version) &&
      Objects.equals(this.contentLength, artifactProperties.contentLength) &&
      Objects.equals(this.contentDigest, artifactProperties.contentDigest) &&
      Objects.equals(this.collectionDate, artifactProperties.collectionDate) &&
      Objects.equals(this.storeDate, artifactProperties.storeDate) &&
      Objects.equals(this.state, artifactProperties.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, uuid, auid, uri, version, contentLength, contentDigest, collectionDate, storeDate, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArtifactProperties {\n");

    sb.append("    namespace: ").append(toIndentedString(namespace)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    auid: ").append(toIndentedString(auid)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    contentLength: ").append(toIndentedString(contentLength)).append("\n");
    sb.append("    contentDigest: ").append(toIndentedString(contentDigest)).append("\n");
    sb.append("    collectionDate: ").append(toIndentedString(collectionDate)).append("\n");
    sb.append("    storeDate: ").append(toIndentedString(storeDate)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

