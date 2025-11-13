package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Versions of the artifacts to return
 */
public enum BulkAuOpEnum {
  START("start"),
    FINISH("finish");

  private String value;

  BulkAuOpEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static BulkAuOpEnum fromValue(String text) {
    for (BulkAuOpEnum b : BulkAuOpEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
