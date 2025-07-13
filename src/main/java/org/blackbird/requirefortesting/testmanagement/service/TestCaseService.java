package org.blackbird.requirefortesting.testmanagement.service;

import java.util.List;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCaseDto;

public interface TestCaseService {
  TestCaseDto createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto, Long userId);

  void deleteTestCase(Long testCaseId);

  TestCaseDto updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto);

  List<TestCaseDto> getAllTestCases();

  TestCaseDto getTestCase(Long id);

  List<TestCaseDto> getTestCasesByRequirementId(Long requirementId);
}
