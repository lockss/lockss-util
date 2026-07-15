package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Output format in Pywb CDX queries
 */
public enum PywbOutputEnum {
  CDX("cdx"),
    JSON("json");

  private String value;

  PywbOutputEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PywbOutputEnum fromValue(String text) {
    for (PywbOutputEnum b : PywbOutputEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
