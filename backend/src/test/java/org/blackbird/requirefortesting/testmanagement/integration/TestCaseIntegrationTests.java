package org.blackbird.requirefortesting.testmanagement.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.blackbird.requirefortesting.TestPostgreSQLContainer;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestCaseServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class TestCaseIntegrationTests {
  @Autowired private TestCaseRepository testCaseRepository;
  @Autowired private TestCaseServiceImpl testCaseServiceImpl;

  private TestCase testCase1, testCase2, testCase3;

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    TestPostgreSQLContainer.configureProperties(registry);
  }

  @BeforeEach
  void setUp() {
    testCase1 = createTestCase("Test Case 1", "Description 1", Status.OPEN);
    testCase2 = createTestCase("Test Case 2", "Description 2", Status.IN_PROGRESS);
    testCase3 = createTestCase("Test Case 3", "Description 3", Status.OPEN);
  }

  @Test
  @Transactional
  void test_saveTestCase_shouldPersistTestCase() {
    String title = "Sample Test Case";
    String description = "This is a sample test case";

    CreateOrUpdateTestCaseDto testCase =
        new CreateOrUpdateTestCaseDto(title, description, 1L, null);

    TestCase createdTestCase = testCaseServiceImpl.createTestCase(testCase, 1L);

    assertNotNull(createdTestCase.getId(), "Saved test case should have an ID");
    assertEquals("Sample Test Case", createdTestCase.getTitle(), "Test case name should match");
  }

  @Test
  @Transactional
  void test_fetchTestCaseById_shouldReturnTestCaseWithTestRuns() {
    TestCase testCase =
        testCaseRepository
            .findById(testCase1.getId())
            .orElseThrow(
                () -> new AssertionError("Test case with ID " + testCase1.getId() + " not found"));

    assertEquals(testCase1.getId(), testCase.getId(), "Test case ID should match");
    assertEquals("Test Case 1", testCase.getTitle(), "Test case title should match");
    assertEquals("Description 1", testCase.getDescription(), "Test case description should match");
  }

  // Edge Case Tests
  @Test
  @Transactional
  void test_findById_withNonExistentId_shouldReturnEmpty() {
    Long nonExistentId = 99999L;

    Optional<TestCase> result = testCaseRepository.findById(nonExistentId);

    assertTrue(result.isEmpty(), "Should return empty for non-existent ID");
  }

  @Test
  @Transactional
  void test_findById_withNegativeId_shouldReturnEmpty() {
    Long negativeId = -1L;

    Optional<TestCase> result = testCaseRepository.findById(negativeId);

    assertTrue(result.isEmpty(), "Should return empty for negative ID");
  }

  @Test
  @Transactional
  void test_createTestCase_withNullTitle_shouldThrowException() {
    CreateOrUpdateTestCaseDto invalidTestCase =
        new CreateOrUpdateTestCaseDto(null, "Description", 1L, Status.OPEN);

    assertThrows(
        Exception.class,
        () -> {
          testCaseServiceImpl.createTestCase(invalidTestCase, 1L);
        },
        "Should throw exception for null title");
  }

  @Test
  @Transactional
  void test_createTestCase_withEmptyTitle_shouldThrowException() {
    CreateOrUpdateTestCaseDto invalidTestCase =
        new CreateOrUpdateTestCaseDto("", "Description", 1L, Status.OPEN);

    assertThrows(
        Exception.class,
        () -> {
          testCaseServiceImpl.createTestCase(invalidTestCase, 1L);
        },
        "Should throw exception for empty title");
  }

  @Test
  @Transactional
  void test_createTestCase_withWhitespaceOnlyTitle_shouldThrowException() {
    CreateOrUpdateTestCaseDto invalidTestCase =
        new CreateOrUpdateTestCaseDto("   ", "Description", 1L, Status.OPEN);

    assertThrows(
        Exception.class,
        () -> {
          testCaseServiceImpl.createTestCase(invalidTestCase, 1L);
        },
        "Should throw exception for whitespace-only title");
  }

  @Test
  @Transactional
  void test_createTestCase_withVeryLongTitle_shouldHandleGracefully() {
    String veryLongTitle = "A".repeat(1000);
    CreateOrUpdateTestCaseDto testCase =
        new CreateOrUpdateTestCaseDto(veryLongTitle, "Description", 1L, Status.OPEN);

    assertThrows(
        Exception.class,
        () -> {
          testCaseServiceImpl.createTestCase(testCase, 1L);
        },
        "Should handle very long titles gracefully");
  }

  @Test
  @Transactional
  void test_createTestCase_withNegativeRequirementId_shouldThrowException() {
    CreateOrUpdateTestCaseDto invalidTestCase =
        new CreateOrUpdateTestCaseDto("Valid Title", "Valid Description", -1L, Status.OPEN);

    assertThrows(
        Exception.class,
        () -> {
          testCaseServiceImpl.createTestCase(invalidTestCase, 1L);
        },
        "Should throw exception for negative requirement ID");
  }

  private TestCase createTestCase(String title, String description, Status status) {
    CreateOrUpdateTestCaseDto testCaseDto =
        new CreateOrUpdateTestCaseDto(title, description, 1L, status);
    return testCaseServiceImpl.createTestCase(testCaseDto, 1L);
  }
}
