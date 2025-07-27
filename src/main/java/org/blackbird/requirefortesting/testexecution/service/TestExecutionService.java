package org.blackbird.requirefortesting.testexecution.service;

import java.util.List;
import org.blackbird.requirefortesting.testexecution.model.TestExecution;
import org.blackbird.requirefortesting.testmanagement.model.TestResult;

public interface TestExecutionService {

  TestExecution assignTestCaseToTester(Long testRunId, Long testCaseId, Long testerId);

  TestExecution submitTestResult(
      Long executionId, Long testerId, TestResult result, String comment);

  List<TestExecution> getExecutionsForTester(Long testerId);

  List<TestExecution> getExecutionsForRun(Long testRunId);
}
