package org.blackbird.requirefortesting.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
  HIGH,
  MEDIUM,
  LOW;

  @JsonCreator
  public static Priority fromString(String value) {
    return Priority.valueOf(value.toUpperCase());
  }

  @JsonValue
  public String toValue() {
    return name();
  }
}
