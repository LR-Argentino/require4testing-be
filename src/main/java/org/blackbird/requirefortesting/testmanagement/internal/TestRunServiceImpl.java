package org.blackbird.requirefortesting.testmanagement.internal;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.internal.validation.TestRunValidator;
import org.blackbird.requirefortesting.testmanagement.internal.validation.ValidationMessage;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.model.TestRunStatus;
import org.blackbird.requirefortesting.testmanagement.service.TestRunService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestRunServiceImpl implements TestRunService {

  private final TestRunRepository testRunRepository;
  private final TestCaseRepository testCaseRepository;

  @Override
  @Transactional
  public TestRun create(CreateTestRunDto testRunDto, Long userId) {
    TestRunValidator.validateNotNull(testRunDto, ValidationMessage.NULL_TEST_RUN_DTO.getMessage());
    TestRunValidator.validateForCreation(testRunDto);
    TestRun createdTestRun = mapToTestRun(testRunDto, userId);
    if (testRunDto.testCaseIds() != null && !testRunDto.testCaseIds().isEmpty()) {
      Set<TestCase> testCases =
          new HashSet<>(testCaseRepository.findAllById(testRunDto.testCaseIds()));
      createdTestRun.setTestCases(testCases);
    }

    return testRunRepository.save(createdTestRun);
  }

  @Override
  @Transactional
  public TestRun update(Long testRunId, CreateTestRunDto testRunDto) {
    TestRunValidator.validateNotNull(testRunId, ValidationMessage.NULL_TEST_ID.getMessage());
    TestRunValidator.validateNotNull(testRunDto, ValidationMessage.NULL_TEST_RUN_DTO.getMessage());

    TestRun existingTestRun = findTestRunById(testRunId);
    updateTestRunFields(existingTestRun, testRunDto);

    return testRunRepository.save(existingTestRun);
  }

  @Override
  @Transactional
  public void delete(Long testRunId) {
    TestRunValidator.validateNotNull(testRunId, ValidationMessage.NULL_TEST_ID.getMessage());
    TestRun testRun = findTestRunById(testRunId);

    testRunRepository.delete(testRun);
  }

  @Override
  @Transactional
  public void addTestCase(Long testRunId, Long testCaseId) {
    if (testCaseId == null || testRunId == null) {
      throw new IllegalArgumentException("Test run ID and test case ID cannot be null");
    }
    TestRun testRun =
        testRunRepository.findById(testRunId).orElseThrow(EntityNotFoundException::new);
    TestCase testCase =
        testCaseRepository.findById(testCaseId).orElseThrow(EntityNotFoundException::new);

    if (testRun.getTestCases() != null
        && !testRun.getTestCases().isEmpty()
        && testRun.getTestCases().contains(testCase)) {
      throw new IllegalStateException("Test case already exists in the test run");
    }

    testRun.getTestCases().add(testCase);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestRun> getAllTestRuns() {
    return testRunRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestRun> getTestRunsByUserId(Long userId) {
    return testRunRepository.findAllByCreatedBy(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public TestRun getTestRunById(Long testRunId) {
    return findTestRunById(testRunId);
  }

  private TestRun findTestRunById(Long testRunId) {
    return testRunRepository.findById(testRunId).orElseThrow(EntityNotFoundException::new);
  }

  private void updateTestRunFields(TestRun existingTestRun, CreateTestRunDto testRunDto) {
    if (testRunDto.title() != null) {
      TestRunValidator.validateTitle(testRunDto.title());
      existingTestRun.setTitle(testRunDto.title());
    }

    if (testRunDto.description() != null) {
      existingTestRun.setDescription(testRunDto.description());
    }

    if (testRunDto.startDate() != null && testRunDto.endDate() != null) {
      TestRunValidator.validateStartAndEndDate(testRunDto);
      existingTestRun.setStartTime(testRunDto.startDate());
      existingTestRun.setEndTime(testRunDto.endDate());
      return;
    }

    if (testRunDto.startDate() != null) {
      TestRunValidator.validateStartDateForUpdate(existingTestRun, testRunDto);
      existingTestRun.setStartTime(testRunDto.startDate());
    }

    if (testRunDto.endDate() != null) {
      TestRunValidator.validateEndDate(existingTestRun, testRunDto);
      existingTestRun.setEndTime(testRunDto.endDate());
    }
  }

  private TestRun mapToTestRun(CreateTestRunDto testRunDto, Long userId) {
    return TestRun.builder()
        .title(testRunDto.title())
        .description(testRunDto.description())
        .startTime(testRunDto.startDate())
        .endTime(testRunDto.endDate())
        .status(TestRunStatus.PLANNED)
        .createdBy(userId)
        .build();
  }
}
