package org.lockss.util.rest.poller.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Type of filename translation to be done
 */
public enum ExportFilenameTranslationEnum {
  NONE("XLATE_NONE"),
    WINDOWS("XLATE_WINDOWS"),
    MAC("XLATE_MAC");

  private String value;

  ExportFilenameTranslationEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ExportFilenameTranslationEnum fromValue(String text) {
    for (ExportFilenameTranslationEnum b : ExportFilenameTranslationEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
