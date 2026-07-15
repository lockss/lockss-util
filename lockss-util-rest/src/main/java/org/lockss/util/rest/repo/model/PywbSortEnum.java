package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Sorting behavior in Pywb CDX queries
 */
public enum PywbSortEnum {
  DEFAULT("default"),
    CLOSEST("closest"),
    REVERSE("reverse");

  private String value;

  PywbSortEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PywbSortEnum fromValue(String text) {
    for (PywbSortEnum b : PywbSortEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
