package org.lockss.util.rest.poller.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The type of archive to create
 */
public enum ExportFileTypeEnum {
  WARC_RESPONSE("WARC_RESPONSE"),
    WARC_RESOURCE("WARC_RESOURCE"),
    ARC_RESPONSE("ARC_RESPONSE"),
    ARC_RESOURCE("ARC_RESOURCE"),
    ZIP("ZIP");

  private String value;

  ExportFileTypeEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ExportFileTypeEnum fromValue(String text) {
    for (ExportFileTypeEnum b : ExportFileTypeEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
