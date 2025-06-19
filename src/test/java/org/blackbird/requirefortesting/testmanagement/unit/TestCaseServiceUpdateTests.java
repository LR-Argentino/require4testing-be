package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestCaseServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceUpdateTests {

  @Mock private TestCaseRepository testManagementRepository;
  @InjectMocks private TestCaseServiceImpl testManagementService;

  @Test
  void test_updateTestCaseWithNull_shouldThrowException() {
    Long testCaseId = 1L;

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.updateTestCase(testCaseId, null);
        });
  }

  @Test
  void test_updateTestCaseOnStatusClosed_shouldThrowException() {
    Long testCaseId = 1L;

    CreateOrUpdateTestCaseDto updateTestCaseDto =
        new CreateOrUpdateTestCaseDto("", "Updated Description", null, Status.OPEN);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.updateTestCase(testCaseId, updateTestCaseDto);
        });
  }

  @Test
  void test_updateTestCaseWithInvalidTitle_shouldThrowException() {
    Long testCaseId = 1L;

    String invalidTitle = "";
    String description = "Updated Description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateOrUpdateTestCaseDto updateTestCaseDto =
        new CreateOrUpdateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.updateTestCase(testCaseId, updateTestCaseDto);
        });
  }

  @Test
  void test_updateTestCaseWithBlankTitle_shouldThrowException() {
    Long testCaseId = 1L;

    String invalidTitle = "   ";
    String description = "Updated Description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateOrUpdateTestCaseDto updateTestCaseDto =
        new CreateOrUpdateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.updateTestCase(testCaseId, updateTestCaseDto);
        });
  }

  @Test
  void test_updateTestCaseOnStatusOpen_shouldReturnUpdatedTestCase() {
    Long testCaseId = 1L;

    TestCase existingTestCase =
        TestCase.builder()
            .id(testCaseId)
            .title("Old Title")
            .description("Old Description")
            .status(Status.OPEN)
            .build();

    when(testManagementRepository.findById(testCaseId)).thenReturn(Optional.of(existingTestCase));

    String validTitle = "Updated Title";
    String description = "Updated Description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    TestCase updatedTestCase =
        TestCase.builder()
            .id(testCaseId)
            .title(validTitle)
            .description(description)
            .requirementId(requirementId)
            .status(status)
            .updatedAt(LocalDateTime.now())
            .build();

    when(testManagementRepository.save(any(TestCase.class))).thenReturn(updatedTestCase);

    CreateOrUpdateTestCaseDto updateTestCaseDto =
        new CreateOrUpdateTestCaseDto(validTitle, description, requirementId, status);

    TestCase result = testManagementService.updateTestCase(testCaseId, updateTestCaseDto);

    assertEquals(validTitle, result.getTitle());
  }

  @Test
  void test_updateChangeStatusToClosed_shouldReturnUpdatedTestCase() {
    Long testCaseId = 1L;

    TestCase existingTestCase =
        TestCase.builder()
            .id(testCaseId)
            .title("Old Title")
            .description("Old Description")
            .status(Status.OPEN)
            .build();

    when(testManagementRepository.findById(testCaseId)).thenReturn(Optional.of(existingTestCase));

    String validTitle = "Updated Title";
    String description = "Updated Description";
    Long requirementId = 1L;
    Status status = Status.CLOSED;

    TestCase updatedTestCase =
        TestCase.builder()
            .id(testCaseId)
            .title(validTitle)
            .description(description)
            .requirementId(requirementId)
            .status(status)
            .updatedAt(LocalDateTime.now())
            .build();

    when(testManagementRepository.save(any(TestCase.class))).thenReturn(updatedTestCase);

    CreateOrUpdateTestCaseDto updateTestCaseDto =
        new CreateOrUpdateTestCaseDto(validTitle, description, requirementId, status);

    TestCase result = testManagementService.updateTestCase(testCaseId, updateTestCaseDto);

    assertEquals(status, result.getStatus());
  }
}
