package org.blackbird.requirefortesting.testmanagement.service;

import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;

public interface TestManagementService {
  TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto);

  TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto);
}
