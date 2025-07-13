package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestCaseServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestCaseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceCreateTests {

  @Mock private TestCaseRepository testManagementRepository;
  @InjectMocks private TestCaseServiceImpl testManagementService;

  @Test
  void test_createTestCaseWithInvalidTitle_shouldThrowException() {
    String invalidTitle = "";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateOrUpdateTestCaseDto createTestCaseDto =
        new CreateOrUpdateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(createTestCaseDto, 1L);
        });
  }

  @Test
  void test_createTestCaseWithWhiteSpaceTitle_shouldThrowException() {
    String invalidTitle = "    ";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateOrUpdateTestCaseDto createTestCaseDto =
        new CreateOrUpdateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(createTestCaseDto, 1L);
        });
  }

  @Test
  void test_createTestCaseWithNull_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(null, 1L);
        });
  }

  @Test
  void test_createTestCaseWithValidData_shouldNotThrowException() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateOrUpdateTestCaseDto createTestCaseDto =
        new CreateOrUpdateTestCaseDto(validTitle, description, requirementId, status);

    assertDoesNotThrow(() -> testManagementService.createTestCase(createTestCaseDto, 1L));
  }

  @Test
  void test_createTestCaseWithValidData_shouldReturnCreatedTestCase() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    TestCase savedTestCase =
        TestCase.builder()
            .id(1L)
            .title("Valid Test Case Title")
            .description("Test description")
            .requirementId(1L)
            .status(Status.OPEN)
            .build();

    when(testManagementRepository.save(any(TestCase.class))).thenReturn(savedTestCase);

    CreateOrUpdateTestCaseDto createTestCaseDto =
        new CreateOrUpdateTestCaseDto(validTitle, description, requirementId, status);

    TestCaseDto result = testManagementService.createTestCase(createTestCaseDto, 1L);

    assertEquals(validTitle, result.title());
    assertEquals(description, result.description());
    assertEquals(status, result.status());
    assertNull(result.testResult());
  }

  @Test
  void test_createTestCaseWithoutStatus_shouldReturnCreatedTestCaseWithStatusOpen() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;

    TestCase savedTestCase =
        TestCase.builder()
            .id(1L)
            .title(validTitle)
            .description(description)
            .requirementId(requirementId)
            .status(Status.OPEN)
            .build();

    when(testManagementRepository.save(any(TestCase.class))).thenReturn(savedTestCase);

    CreateOrUpdateTestCaseDto createTestCaseDto =
        new CreateOrUpdateTestCaseDto(validTitle, description, requirementId, null);

    TestCaseDto result = testManagementService.createTestCase(createTestCaseDto, 1L);

    assertEquals(requirementId, result.requirementId());
    assertEquals(validTitle, result.title());
    assertEquals(description, result.description());
    assertEquals(Status.OPEN, result.status());
    assertNull(result.testResult());
  }
}
