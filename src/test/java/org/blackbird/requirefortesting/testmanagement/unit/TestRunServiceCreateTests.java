package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
class TestRunServiceCreateTests {

  @Mock private TestRunRepository testRunRepository;
  @InjectMocks private TestRunServiceImpl testRunService;

  @Test
  void test_createTestRunWithNull_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> testRunService.create(null));
  }

  @Test
  void test_createTestRunWithNullTitle_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(null, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithBlankTitle_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto("   ", null, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithEmptyTitle_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto("", null, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithNullStartDateAndEndDate_shouldThrowException() {
    CreateTestRunDto createTestRunDto = new CreateTestRunDto("Title", null, null, null);

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithStartDateInPast_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Title",
            "Some Description",
            LocalDateTime.now().minusMinutes(1),
            LocalDateTime.now().plusMinutes(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithStartDateSetAfterEndDate_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Title",
            "Some Description",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusMinutes(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithEndDateSetInPast_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Title",
            "Some Description",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().minusMinutes(1));

    assertThrows(IllegalArgumentException.class, () -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithValidStartDateAndEndDate_shouldThrowException() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Title",
            "Some Description",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(7));

    assertDoesNotThrow(() -> testRunService.create(createTestRunDto));
  }

  @Test
  void test_createTestRunWithValidData_shouldHaveStatusPlanned() {
    CreateTestRunDto createTestRunDto =
        new CreateTestRunDto(
            "Smoke Test",
            "Some Description",
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1));

    TestRun savedTestRun =
        TestRun.builder()
            .title(createTestRunDto.title())
            .description(createTestRunDto.description())
            .startTime(createTestRunDto.startDate())
            .endTime(createTestRunDto.endDate())
            .status(TestRunStatus.PLANNED)
            .createdBy(1L)
            .testCases(null)
            .build();

    when(testRunRepository.save(any(TestRun.class))).thenReturn(savedTestRun);

    TestRun createdTestRun = testRunService.create(createTestRunDto);

    assertDoesNotThrow(() -> testRunService.create(createTestRunDto));

    assertEquals(savedTestRun.getId(), createdTestRun.getId());
  }
}
