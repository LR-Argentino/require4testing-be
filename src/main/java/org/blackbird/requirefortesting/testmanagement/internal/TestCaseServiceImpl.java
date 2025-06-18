package org.blackbird.requirefortesting.testmanagement.internal;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.service.TestCaseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

  private final TestCaseRepository testManagementRepository;

  @Override
  public TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    validateTestCaseDto(createTestCaseDto);

    return testManagementRepository.save(mapToTestCase(createTestCaseDto));
  }

  @Override
  public TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto) {
    validateTestCaseDto(updateTestCaseDto);

    TestCase testCaseFromDb =
        testManagementRepository
            .findById(testCaseId)
            .orElseThrow(
                () -> new IllegalArgumentException("Test case not found with id: " + testCaseId));

    if (testCaseFromDb.getStatus() == Status.CLOSED) {
      throw new IllegalArgumentException("Cannot update a closed test case");
    }

    testCaseFromDb.setTitle(updateTestCaseDto.title());
    testCaseFromDb.setDescription(updateTestCaseDto.description());
    testCaseFromDb.setReuqirementId(updateTestCaseDto.requirementId());

    if (updateTestCaseDto.status() != null) {
      testCaseFromDb.setStatus(updateTestCaseDto.status());
    }

    return testManagementRepository.save(testCaseFromDb);
  }

  @Override
  public void addTestCaseToTestRun(Long testRunId, Long testCaseId) {}

  private TestCase mapToTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    TestCase testCase =
        TestCase.builder()
            .title(createTestCaseDto.title())
            .description(createTestCaseDto.description())
            .status(createTestCaseDto.status() != null ? createTestCaseDto.status() : Status.OPEN)
            .reuqirementId(createTestCaseDto.requirementId())
            .build();
    return testCase;
  }

  // TODO: Use TestRunValidator for validation
  private void validateTestCaseDto(CreateOrUpdateTestCaseDto testCaseDto) {
    if (testCaseDto == null) {
      throw new IllegalArgumentException("UpdateTestCaseDto cannot be null");
    }
    if (testCaseDto.title() == null || testCaseDto.title().isBlank()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
  }
}
