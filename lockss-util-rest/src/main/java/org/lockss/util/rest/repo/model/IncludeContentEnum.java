package org.lockss.util.rest.repo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Whether to include the artifact content part in a multipart response
 */
public enum IncludeContentEnum {
  NEVER("NEVER"),
    IF_SMALL("IF_SMALL"),
    ALWAYS("ALWAYS");

  private String value;

  IncludeContentEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static IncludeContentEnum fromValue(String text) {
    for (IncludeContentEnum b : IncludeContentEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
