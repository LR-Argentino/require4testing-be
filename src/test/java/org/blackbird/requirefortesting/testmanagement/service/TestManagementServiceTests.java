package org.blackbird.requirefortesting.testmanagement.service;

import static org.junit.jupiter.api.Assertions.*;

import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestManagementServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestManagementRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest(module = "testmanagement")
public class TestManagementServiceTests {

  @Mock private TestManagementRepository testManagementRepository;
  @InjectMocks private TestManagementServiceImpl testManagementService;

  @Test
  void test_createTestCaseWithInvalidTitle_shouldThrowException() {
    String invalidTitle = "";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateTestCaseDto createTestCaseDto =
        new CreateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(createTestCaseDto);
        });
  }

  @Test
  void test_createTestCaseWithWhiteSpaceTitle_shouldThrowException() {
    String invalidTitle = "    ";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateTestCaseDto createTestCaseDto =
        new CreateTestCaseDto(invalidTitle, description, requirementId, status);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(createTestCaseDto);
        });
  }

  @Test
  void test_createTestCaseWithNull_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          testManagementService.createTestCase(null);
        });
  }

  @Test
  void test_createTestCaseWithValidData_shouldNotThrowException() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateTestCaseDto createTestCaseDto =
        new CreateTestCaseDto(validTitle, description, requirementId, status);

    assertDoesNotThrow(() -> testManagementService.createTestCase(createTestCaseDto));
  }

  @Test
  void test_createTestCaseWithValidData_shouldReturnCreatedTestCase() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;
    Status status = Status.OPEN;

    CreateTestCaseDto createTestCaseDto =
        new CreateTestCaseDto(validTitle, description, requirementId, status);

    TestCase result = testManagementService.createTestCase(createTestCaseDto);

    assertEquals(requirementId, result.getReuqirementId());
    assertEquals(validTitle, result.getTitle());
    assertEquals(description, result.getDescription());
    assertEquals(status, result.getStatus());
    assertNull(result.getTestResult());
  }

  @Test
  void test_createTestCaseWithoutStatus_shouldReturnCreatedTestCaseWithStatusOpen() {
    String validTitle = "Valid Test Case Title";
    String description = "Test description";
    Long requirementId = 1L;

    CreateTestCaseDto createTestCaseDto =
        new CreateTestCaseDto(validTitle, description, requirementId, null);

    TestCase result = testManagementService.createTestCase(createTestCaseDto);

    assertEquals(requirementId, result.getReuqirementId());
    assertEquals(validTitle, result.getTitle());
    assertEquals(description, result.getDescription());
    assertEquals(Status.OPEN, result.getStatus());
    assertNull(result.getTestResult());
  }
}
