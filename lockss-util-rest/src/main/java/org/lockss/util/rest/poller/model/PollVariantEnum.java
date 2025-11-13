package org.lockss.util.rest.poller.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The V3 poll variant.
 */
public enum PollVariantEnum {
  POR("PoR"),
    POP("PoP"),
    LOCAL("Local"),
    NOPOLL("NoPoll");

  private String value;

  PollVariantEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PollVariantEnum fromValue(String text) {
    for (PollVariantEnum b : PollVariantEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
