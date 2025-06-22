package org.blackbird.requirefortesting.testmanagement.internal;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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

    TestCase testCaseFromDb = getTestCase(testCaseId);

    if (testCaseFromDb.getStatus() == Status.CLOSED) {
      throw new IllegalArgumentException("Cannot update a closed test case");
    }

    updateTestCase(testCaseFromDb, updateTestCaseDto);

    return testCaseRepository.save(testCaseFromDb);
  }

  @Override
  @Transactional
  public void deleteTestCase(Long testCaseId) {
    if (testCaseId == null || testCaseId <= 0) {
      throw new IllegalArgumentException("Test case ID cannot be null or negative");
    }

    TestCase testCaseFromDb = getTestCase(testCaseId);

    testCaseRepository.delete(testCaseFromDb);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestCase> getAllTestCases() {
    return testCaseRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public TestCase getTestCase(Long id) {
    return testCaseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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
    } else if (testCaseDto.title().length() > 255) {
      throw new IllegalArgumentException("Title cannot exceed 255 characters");
    }

    if (testCaseDto.requirementId() == null || testCaseDto.requirementId() <= 0) {
      throw new IllegalArgumentException("Requirement ID must be greater than 0");
    }
  }

  private void updateTestCase(
      TestCase existingTestCase, CreateOrUpdateTestCaseDto updateTestCaseDto) {

    if (updateTestCaseDto.title() != null) {
      existingTestCase.setTitle(updateTestCaseDto.title());
    }

    if (updateTestCaseDto.description() != null) {
      existingTestCase.setDescription(updateTestCaseDto.description());
    }

    if (updateTestCaseDto.status() != null) {
      existingTestCase.setStatus(updateTestCaseDto.status());
    }

    if (updateTestCaseDto.requirementId() != null) {
      existingTestCase.setRequirementId(updateTestCaseDto.requirementId());
    }
  }
}
