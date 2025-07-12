package org.blackbird.requirefortesting.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
  OPEN,
  IN_PROGRESS,
  CLOSED;

  @JsonCreator
  public static Status fromString(String value) {
    return Status.valueOf(value.toUpperCase());
  }

  @JsonValue
  public String toValue() {
    return name();
  }
}
