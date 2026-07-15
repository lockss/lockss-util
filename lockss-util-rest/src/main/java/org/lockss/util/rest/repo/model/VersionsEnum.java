package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Versions of the artifacts to return
 */
public enum VersionsEnum {
  ALL("all"),
    LATEST("latest");

  private String value;

  VersionsEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static VersionsEnum fromValue(String text) {
    for (VersionsEnum b : VersionsEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
