package org.blackbird.requirefortesting.testmanagement.internal.validation;

import java.time.LocalDateTime;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.model.TestRunStatus;

public class TestRunValidator {
  private static final int BUFFER_FOR_CLOCK_SKEW_SECONDS = 30;

  public static void validateNotNull(Object value, String message) {
    if (value == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void validateForCreation(CreateTestRunDto testRunDto) {
    validateTitle(testRunDto.title());
    validateDatesNotNull(testRunDto);
    validateStartDate(testRunDto);
  }

  public static void validateTitle(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Test run title cannot be null or blank");
    }
    if (title.length() > 255) {
      throw new IllegalArgumentException("Test run title cannot exceed 255 characters");
    }
  }

  public static void validateDatesNotNull(CreateTestRunDto testRunDto) {
    if (testRunDto.startDate() == null || testRunDto.endDate() == null) {
      throw new IllegalArgumentException("Test run start and end dates cannot be null");
    }
  }

  public static void validateStartDate(CreateTestRunDto testRunDto) {
    LocalDateTime now = LocalDateTime.now();
    if (testRunDto.startDate() != null
        && testRunDto.startDate().isBefore(now.minusSeconds(BUFFER_FOR_CLOCK_SKEW_SECONDS))) {
      throw new IllegalArgumentException("Test run start date cannot be in the past");
    }
    if (testRunDto.endDate() != null && testRunDto.startDate().isAfter(testRunDto.endDate())) {
      throw new IllegalArgumentException("Test run start date cannot be after end date");
    }
    if (testRunDto.startDate() == testRunDto.endDate()) {
      throw new IllegalArgumentException("Test run start date cannot be the same as end date");
    }
  }

  public static void validateEndDate(TestRun existingTestRun, CreateTestRunDto testRunDto) {
    LocalDateTime now = LocalDateTime.now();
    if (existingTestRun.getStatus() == TestRunStatus.COMPLETED) {
      throw new IllegalArgumentException("Cannot update end date for a completed test run");
    }

    if (testRunDto.endDate().isBefore(existingTestRun.getStartTime())
        || testRunDto.endDate().isBefore(now.minusSeconds(BUFFER_FOR_CLOCK_SKEW_SECONDS))) {
      throw new IllegalArgumentException(
          "Test run end date cannot be before the start date of the existing test run or in the past");
    }
  }

  public static void validateStartDateForUpdate(
      TestRun existingTestRun, CreateTestRunDto testRunDto) {
    if (existingTestRun.getStatus() != TestRunStatus.PLANNED) {
      throw new IllegalArgumentException("Cannot update start date for a non-planned test run");
    }

    if (isStartDateAfterEndDate(existingTestRun, testRunDto)) {
      throw new IllegalArgumentException(
          "Test run start date cannot be after the end date of the existing test run");
    }

    validateStartDate(testRunDto);
  }

  public static void validateStartAndEndDate(CreateTestRunDto testRunDto) {
    if (testRunDto.startDate().isAfter(testRunDto.endDate())) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }
    if (testRunDto.startDate().equals(testRunDto.endDate())) {
      throw new IllegalArgumentException("Start date cannot be the same as end date");
    }
  }

  private static boolean isStartDateAfterEndDate(
      TestRun existingTestRun, CreateTestRunDto testRunDto) {
    return testRunDto.startDate() != null
        && existingTestRun.getEndTime() != null
        && testRunDto.startDate().isAfter(existingTestRun.getEndTime());
  }
}
