package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.blackbird.requirefortesting.testmanagement.internal.TestRunServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.model.TestRunStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestRunServiceUpdateTests {

  @Mock private TestRunRepository testRunRepository;
  @InjectMocks private TestRunServiceImpl testRunService;

  @Test
  void test_updateTestRunWithNullIdAndNullDto_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> testRunService.update(null, null));
  }

  @Test
  void test_updateTestRunWithWithNotExistingId_shouldThrowException() {
    CreateTestRunDto createTestRunDto = new CreateTestRunDto("Test Run", null, null, null, null);
    Long nonExistingId = 999L;

    when(testRunRepository.findById(nonExistingId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> testRunService.update(nonExistingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunWithInvalidTitle_shouldThrowException() {
    CreateTestRunDto createTestRunDto = new CreateTestRunDto("  ", null, null, null, null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.PLANNED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().minusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunWithInvalidDate_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Some Test",
            "Some description",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.PLANNED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().minusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunOnStatusPlannedAndStartDateInPast_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Some Test", "Some description", LocalDateTime.now().minusDays(1), null, null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.PLANNED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().minusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunOnStatusInProgressAndStartDateInPast_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Some Test", "Some description", LocalDateTime.now().minusDays(1), null, null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().minusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunWithStartDateInPast_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, LocalDateTime.now().plusDays(1), null, null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.PLANNED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusHours(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunsEndDateOnStatusCompleted_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, null, LocalDateTime.now().plusDays(3), null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.COMPLETED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunsEndDateOnStatusInProgress_shouldUpdateEndDate() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, null, LocalDateTime.now().plusDays(3), null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));
    when(testRunRepository.save(any(TestRun.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    TestRun updatedTestRun = testRunService.update(existingId, createTestRunDto);

    assertEquals(createTestRunDto.endDate(), updatedTestRun.getEndTime());
  }

  @Test
  void test_updateTestRunsEndDateOnStatusInPlanned_shouldUpdateEndDate() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, null, LocalDateTime.now().plusDays(3), null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.PLANNED)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));
    when(testRunRepository.save(any(TestRun.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    TestRun updatedTestRun = testRunService.update(existingId, createTestRunDto);

    assertEquals(createTestRunDto.endDate(), updatedTestRun.getEndTime());
  }

  @Test
  void test_updateTestRunsEndDateBeforeStartDate_shouldUpdateEndDate() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, null, LocalDateTime.now().minusDays(3), null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now().plusDays(1))
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }

  @Test
  void test_updateTestRunsEndDateIsPast_shouldUpdateEndDate() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, null, LocalDateTime.now().minusMinutes(1), null);
    TestRun fetchedTestRun =
        TestRun.builder()
            .title("Smoke Teste")
            .description("Some description")
            .status(TestRunStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now().minusDays(2))
            .endTime(LocalDateTime.now())
            .createdBy(1L)
            .build();
    Long existingId = 1L;

    when(testRunRepository.findById(existingId)).thenReturn(Optional.of(fetchedTestRun));

    assertThrows(
        IllegalArgumentException.class, () -> testRunService.update(existingId, createTestRunDto));
  }
}
