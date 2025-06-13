package org.blackbird.requirefortesting.testmanagement.service;

import org.blackbird.requirefortesting.testmanagement.model.CreateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;

public interface TestManagementService {
  TestCase createTestCase(CreateTestCaseDto createTestCaseDto);
}
