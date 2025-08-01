package org.blackbird.requirefortesting.testmanagement.service;

import java.util.List;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;

public interface TestRunService {
  TestRun create(CreateTestRunDto testRunDto, Long userId);

  TestRun update(Long testRunId, CreateTestRunDto testRunDto);

  void delete(Long testRunId);

  List<TestRun> getAllTestRuns();

  List<TestRun> getTestRunsByUserId(Long userId);

  TestRun getTestRunById(Long testRunId);

  void addTestCase(Long testRunId, Long testCaseId);
}
