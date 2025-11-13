package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Match type in Pywb CDX queries
 */
public enum PywbMatchEnum {
  EXACT("exact"),
    PREFIX("prefix"),
    HOST("host"),
    DOMAIN("domain"),
    RANGE("range");

  private String value;

  PywbMatchEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PywbMatchEnum fromValue(String text) {
    for (PywbMatchEnum b : PywbMatchEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
