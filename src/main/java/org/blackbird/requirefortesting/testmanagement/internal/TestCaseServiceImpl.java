package org.blackbird.requirefortesting.testmanagement.internal;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.service.TestCaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

  private final TestCaseRepository testCaseRepository;

  @Override
  @Transactional
  public TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    validateTestCaseDto(createTestCaseDto);

    return testCaseRepository.save(mapToTestCase(createTestCaseDto));
  }

  @Override
  @Transactional
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
    testCaseFromDb.setRequirementId(updateTestCaseDto.requirementId());

    if (updateTestCaseDto.status() != null) {
      testCaseFromDb.setStatus(updateTestCaseDto.status());
    }

    return testCaseRepository.save(testCaseFromDb);
  }

  private TestCase mapToTestCase(CreateOrUpdateTestCaseDto createTestCaseDto) {
    TestCase testCase =
        TestCase.builder()
            .title(createTestCaseDto.title())
            .description(createTestCaseDto.description())
            .status(createTestCaseDto.status() != null ? createTestCaseDto.status() : Status.OPEN)
            .requirementId(createTestCaseDto.requirementId())
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
