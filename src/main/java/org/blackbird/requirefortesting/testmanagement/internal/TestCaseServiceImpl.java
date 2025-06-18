package org.blackbird.requirefortesting.testmanagement.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.service.TestCaseService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

  private final TestCaseRepository testCaseRepository;
  private final TestRunRepository testRunRepository;

  @Override
  public TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    validateTestCaseDto(createTestCaseDto);

    return testCaseRepository.save(mapToTestCase(createTestCaseDto));
  }

  @Override
  public TestCase updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto) {
    validateTestCaseDto(updateTestCaseDto);

    TestCase testCaseFromDb =
        testCaseRepository
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

    return testCaseRepository.save(testCaseFromDb);
  }

  // TODO: Move to TestRunService
  @Override
  public void addTestCaseToTestRun(Long testRunId, Long testCaseId) {
    if (testCaseId == null || testRunId == null) {
      throw new IllegalArgumentException("Test run ID and test case ID cannot be null");
    }
    TestRun testRun =
        testRunRepository.findById(testRunId).orElseThrow(EntityNotFoundException::new);
    TestCase testCase =
        testCaseRepository.findById(testCaseId).orElseThrow(EntityNotFoundException::new);

    if (testRun.getTestCases() != null
        && !testRun.getTestCases().isEmpty()
        && testRun.getTestCases().contains(testCase)) {
      throw new IllegalStateException("Test case already exists in the test run");
    }

    testRun.getTestCases().add(testCase);
  }

  private TestCase mapToTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    TestCase testCase =
        TestCase.builder()
            .title(createTestCaseDto.title())
            .description(createTestCaseDto.description())
            .status(createTestCaseDto.status() != null ? createTestCaseDto.status() : Status.OPEN)
            .reuqirementId(createTestCaseDto.requirementId())
            .createdBy(1L) // TODO: Replace with actual user ID
            .build();
    return testCase;
  }

  private void validateTestCaseDto(CreateOrUpdateTestCaseDto testCaseDto) {
    if (testCaseDto == null) {
      throw new IllegalArgumentException("UpdateTestCaseDto cannot be null");
    }
    if (testCaseDto.title() == null || testCaseDto.title().isBlank()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }

    if (testCaseDto.requirementId() <= 0) {
      throw new IllegalArgumentException("Requirement ID must be greater than 0");
    }
  }
}
