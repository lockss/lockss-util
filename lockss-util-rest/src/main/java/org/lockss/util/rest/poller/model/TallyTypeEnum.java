package org.lockss.util.rest.poller.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The kind of tally element to return
 */
public enum TallyTypeEnum {
  AGREE("agree"),
    DISAGREE("disagree"),
    ERROR("error"),
    NOQUORUM("noQuorum"),
    TOOCLOSE("tooClose");

  private String value;

  TallyTypeEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static TallyTypeEnum fromValue(String text) {
    for (TallyTypeEnum b : TallyTypeEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
