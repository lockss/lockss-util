package org.lockss.util.rest.crawler;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The kind of crawl being performed either 'newContent' or 'repair'.
 */
public enum CrawlKindEnum {
  NEWCONTENT("newContent"),
    REPAIR("repair");

  private String value;

  CrawlKindEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CrawlKindEnum fromValue(String text) {
    for (CrawlKindEnum b : CrawlKindEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
