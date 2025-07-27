package org.blackbird.requirefortesting.testexecution.internal;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testexecution.internal.repository.TestExecutionRepository;
import org.blackbird.requirefortesting.testexecution.model.TestExecution;
import org.blackbird.requirefortesting.testexecution.service.TestExecutionService;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestResult;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestExecutionServiceImpl implements TestExecutionService {

  private final TestExecutionRepository executionRepository;
  private final TestRunRepository testRunRepository;
  private final TestCaseRepository testCaseRepository;

  @Override
  @Transactional
  public TestExecution assignTestCaseToTester(Long testRunId, Long testCaseId, Long testerId) {
    if (testRunId == null || testCaseId == null || testerId == null) {
      throw new IllegalArgumentException(
          "Test run id, test case id and tester id must all be provided");
    }

    TestRun testRun =
        testRunRepository.findById(testRunId).orElseThrow(EntityNotFoundException::new);
    TestCase testCase =
        testCaseRepository.findById(testCaseId).orElseThrow(EntityNotFoundException::new);

    TestExecution existing =
        executionRepository.findByTestRunIdAndTestCaseIdAndTesterId(
            testRunId, testCaseId, testerId);
    if (existing != null) {
      return existing;
    }

    TestExecution execution =
        TestExecution.builder().testRun(testRun).testCase(testCase).testerId(testerId).build();
    return executionRepository.save(execution);
  }

  @Override
  @Transactional
  public TestExecution submitTestResult(
      Long executionId, Long testerId, TestResult result, String comment) {
    if (executionId == null || testerId == null) {
      throw new IllegalArgumentException("Execution id and tester id must both be provided");
    }
    TestExecution execution =
        executionRepository
            .findById(executionId)
            .orElseThrow(() -> new EntityNotFoundException("Test execution not found"));
    // Only the assigned tester can submit a result
    if (!execution.getTesterId().equals(testerId)) {
      throw new IllegalArgumentException("User is not assigned to this execution");
    }

    execution.setTestResult(result);
    execution.setComment(comment);
    return executionRepository.save(execution);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestExecution> getExecutionsForTester(Long testerId) {
    if (testerId == null) {
      throw new IllegalArgumentException("Tester id must be provided");
    }
    return executionRepository.findByTesterId(testerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestExecution> getExecutionsForRun(Long testRunId) {
    if (testRunId == null) {
      throw new IllegalArgumentException("Test run id must be provided");
    }
    return executionRepository.findByTestRunId(testRunId);
  }
}
