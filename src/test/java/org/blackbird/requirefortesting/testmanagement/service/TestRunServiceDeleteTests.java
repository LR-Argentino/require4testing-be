package org.blackbird.requirefortesting.testmanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestRunServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.model.TestRunStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestRunServiceDeleteTests {

  @Mock private TestRunRepository testRunRepository;
  @InjectMocks private TestRunServiceImpl testRunService;

  @Test
  void test_deleteTestRunWithNullId_shouldThrowException() {
    Long testRunId = null;

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testRunService.delete(testRunId);
        });
  }

  @Test
  void test_deleteTestRunWithNonExistingEntity_shouldThrowException() {
    Long testRunId = 999L;

    assertThrows(
        EntityNotFoundException.class,
        () -> {
          testRunService.delete(testRunId);
        });
  }

  @Test
  void test_deleteTestRun_shouldDeleteTestRun() {
    Long testRunId = 1L;
    Long testCaseId = 10L;
    TestCase testCase =
        TestCase.builder()
            .id(testCaseId)
            .title("Smoke Test Case")
            .status(Status.OPEN)
            .creationDate(LocalDateTime.now().minusDays(2))
            .build();

    TestRun testRun =
        TestRun.builder()
            .id(testRunId)
            .title("Project Test Run")
            .description("Test run for project")
            .status(TestRunStatus.PLANNED)
            .testCases(Set.of(testCase))
            .startTime(LocalDateTime.now().plusMinutes(1))
            .endTime(LocalDateTime.now().plusMinutes(2))
            .build();

    when(testRunRepository.findById(testRunId)).thenReturn(Optional.of(testRun));

    assertDoesNotThrow(() -> testRunService.delete(testRunId));
  }
}
