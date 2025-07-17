package org.blackbird.requirefortesting.testmanagement.internal;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestCaseDto;
import org.blackbird.requirefortesting.testmanagement.service.TestCaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

  private final TestCaseRepository testCaseRepository;

  @Override
  @Transactional
  public TestCase createTestCase(CreateOrUpdateTestCaseDto createTestCaseDto, Long userId) {
    validateTestCaseDto(createTestCaseDto);
    TestCase savedTestCase = testCaseRepository.save(mapToTestCase(createTestCaseDto, userId));

    return savedTestCase;
  }

  @Override
  @Transactional
  public TestCaseDto updateTestCase(Long testCaseId, CreateOrUpdateTestCaseDto updateTestCaseDto) {
    validateTestCaseDto(updateTestCaseDto);

    TestCase testCaseFromDb =
        testCaseRepository.findById(testCaseId).orElseThrow(EntityNotFoundException::new);

    if (testCaseFromDb.getStatus() == Status.CLOSED) {
      throw new IllegalArgumentException("Cannot update a closed test case");
    }

    updateTestCase(testCaseFromDb, updateTestCaseDto);
    TestCase savedTestCase = testCaseRepository.save(testCaseFromDb);

    return mapToDto(savedTestCase);
  }

  @Override
  @Transactional
  public void deleteTestCase(Long testCaseId) {
    if (testCaseId == null || testCaseId <= 0) {
      throw new IllegalArgumentException("Test case ID cannot be null or negative");
    }

    TestCase testCaseFromDb =
        testCaseRepository.findById(testCaseId).orElseThrow(EntityNotFoundException::new);

    testCaseRepository.delete(testCaseFromDb);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestCaseDto> getAllTestCases() {
    return testCaseRepository.findAll().stream().map(TestCaseServiceImpl::mapToDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TestCaseDto getTestCase(Long id) {
    TestCase testCase = testCaseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    return mapToDto(testCase);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TestCaseDto> getTestCasesByRequirementId(Long requirementId) {
    if (requirementId == null || requirementId <= 0) {
      throw new IllegalArgumentException("Requirement case ID cannot be null or negative");
    }
    return testCaseRepository.findTestCasesByRequirementId(requirementId).stream()
        .map(TestCaseServiceImpl::mapToDto)
        .toList();
  }

  private static TestCase mapToTestCase(CreateOrUpdateTestCaseDto createTestCaseDto, Long userId) {
    return TestCase.builder()
        .title(createTestCaseDto.title())
        .description(createTestCaseDto.description())
        .status(createTestCaseDto.status() != null ? createTestCaseDto.status() : Status.OPEN)
        .requirementId(createTestCaseDto.requirementId())
        .createdBy(userId)
        .build();
  }

  private static void validateTestCaseDto(CreateOrUpdateTestCaseDto testCaseDto) {
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

  private static void updateTestCase(
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

  private static TestCaseDto mapToDto(TestCase testCase) {
    return TestCaseDto.builder()
        .id(testCase.getId())
        .title(testCase.getTitle())
        .description(testCase.getDescription())
        .requirementId(testCase.getRequirementId())
        .status(testCase.getStatus())
        .testResult(testCase.getTestResult())
        .createdBy(testCase.getCreatedBy())
        .updatedAt(testCase.getUpdatedAt())
        .creationDate(testCase.getCreationDate())
        .build();
  }
}
