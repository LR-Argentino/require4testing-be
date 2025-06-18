package org.blackbird.requirefortesting.testmanagement.service;

import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;

public interface TestCaseService {
  TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto);

  TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto);

  void addTestCaseToTestRun(Long testRunId, Long testCaseId);
}
