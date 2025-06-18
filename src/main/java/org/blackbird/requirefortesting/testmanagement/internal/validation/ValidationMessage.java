package org.blackbird.requirefortesting.testmanagement.internal.validation;

public enum ValidationMessage {
  NULL_TEST_ID("Test ID cannot be null"),
  NULL_TEST_RUN_DTO("Test run DTO cannot be null");

  ValidationMessage(String message) {}
}
