package org.blackbird.requirefortesting.testmanagement.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.blackbird.requirefortesting.TestPostgreSQLContainer;
import org.blackbird.requirefortesting.testmanagement.internal.TestRunServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestRunIntegrationTests {
  @Autowired private TestRunRepository testRunRepository;
  @Autowired private TestRunServiceImpl testRunService;

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    TestPostgreSQLContainer.configureProperties(registry);
  }

  @Test
  @Transactional
  void test_saveTestRun_shouldPersistTestCase() {
    String title = "Sample Test Run";
    String description = "This is a sample test run";
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);

    CreateTestRunDto testRunDto = new CreateTestRunDto(title, description, startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId(), "Saved test run should have an ID");
    assertEquals("Sample Test Run", createdTestRun.getTitle(), "Test run name should match");
  }

  @Test
  @Transactional
  void test_createTestRun_withNullTitle_shouldThrowException() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto invalidTestCase =
        new CreateTestRunDto(null, "Description", startTime, endTime);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception for null title");
  }

  @Test
  @Transactional
  void test_createTestRun_withEndTimeBeforeStartTime_shouldThrowException() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.minusDays(1);
    CreateTestRunDto invalidTestCase =
        new CreateTestRunDto("Valid Title", "Description", startTime, endTime);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception when end time is before start time");
  }

  @Test
  @Transactional
  void test_createTestRun_withNullStartTime_shouldThrowException() {
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto invalidTestCase =
        new CreateTestRunDto("Valid Title", "Description", null, endTime);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception for null start time");
  }

  @Test
  @Transactional
  void test_createTestRun_withNullEndTime_shouldThrowException() {
    LocalDateTime startTime = LocalDateTime.now();
    CreateTestRunDto invalidTestCase =
        new CreateTestRunDto("Valid Title", "Description", startTime, null);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception for null end time");
  }

  @Test
  @Transactional
  void test_createTestRun_withEmptyTitle_shouldThrowException() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto invalidTestCase = new CreateTestRunDto("", "Description", startTime, endTime);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception for empty title");
  }

  @Test
  @Transactional
  void test_createTestRun_withNullDescription_shouldSucceed() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto testRunDto = new CreateTestRunDto("Valid Title", null, startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals("Valid Title", createdTestRun.getTitle());
    assertNull(createdTestRun.getDescription());
  }

  @Test
  @Transactional
  void test_createTestRun_withEmptyDescription_shouldSucceed() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto testRunDto = new CreateTestRunDto("Valid Title", "", startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals("Valid Title", createdTestRun.getTitle());
    assertEquals("", createdTestRun.getDescription());
  }

  @Test
  @Transactional
  void test_createTestRun_withSpecialCharactersInTitle_shouldSucceed() {
    String specialTitle = "Test@#$%^&*()_+-={}[]|\\:;\"'<>?,./`~";
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto testRunDto =
        new CreateTestRunDto(specialTitle, "Description", startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals(specialTitle, createdTestRun.getTitle());
  }

  @Test
  @Transactional
  void test_createTestRun_withUnicodeCharacters_shouldSucceed() {
    String unicodeTitle = "Теsт 测试";
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now().plusDays(10);
    CreateTestRunDto testRunDto =
        new CreateTestRunDto(unicodeTitle, "Unicode описание", startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals(unicodeTitle, createdTestRun.getTitle());
  }

  @Test
  @Transactional
  void test_createTestRun_withSameStartAndEndTime_shouldThrowException() {
    LocalDateTime sameTime = LocalDateTime.now();
    CreateTestRunDto invalidTestCase =
        new CreateTestRunDto("Valid Title", "Description", sameTime, sameTime);

    assertThrows(
        Exception.class,
        () -> {
          testRunService.create(invalidTestCase);
        },
        "Should throw exception when start and end time are the same");
  }

  @Test
  @Transactional
  void test_createTestRun_withPastStartTime_shouldThrowException() {
    LocalDateTime pastStartTime = LocalDateTime.now().minusDays(5);
    LocalDateTime futureEndTime = LocalDateTime.now().plusDays(5);
    CreateTestRunDto testRunDto =
        new CreateTestRunDto("Past Start Test", "Description", pastStartTime, futureEndTime);

    assertThrows(Exception.class, () -> testRunService.create(testRunDto));
  }

  @Test
  @Transactional
  void test_createTestRun_withVeryFarFutureEndTime_shouldSucceed() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime farFutureEndTime = LocalDateTime.now().plusYears(100);
    CreateTestRunDto testRunDto =
        new CreateTestRunDto("Future Test", "Description", startTime, farFutureEndTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals("Future Test", createdTestRun.getTitle());
  }

  @Test
  @Transactional
  void test_createTestRun_withMinimalValidDuration_shouldSucceed() {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusMinutes(1);
    CreateTestRunDto testRunDto =
        new CreateTestRunDto("Minimal Duration", "Description", startTime, endTime);

    TestRun createdTestRun = testRunService.create(testRunDto);

    assertNotNull(createdTestRun.getId());
    assertEquals("Minimal Duration", createdTestRun.getTitle());
  }
}
