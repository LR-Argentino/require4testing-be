package org.blackbird.requirefortesting.testmanagement.service;

import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;

public interface TestCaseService {
  TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto);

  void deleteTestCase(Long testCaseId);

  TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto);
}
