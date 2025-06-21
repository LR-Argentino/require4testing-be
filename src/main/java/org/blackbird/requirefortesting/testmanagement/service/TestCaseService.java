package org.blackbird.requirefortesting.testmanagement.service;

import java.util.List;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;

public interface TestCaseService {
  TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto);

  void deleteTestCase(Long testCaseId);

  TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto);

  List<TestCase> getAllTestCases();

  TestCase getTestCase(Long id);
}
