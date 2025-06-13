package org.blackbird.requirefortesting.testmanagement.internal;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestManagementRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.service.TestManagementService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestManagementServiceImpl implements TestManagementService {

  private final TestManagementRepository testManagementRepository;

  @Override
  public TestCase createTestCase(CreateTestCaseDto createTestCaseDto) {

    if (createTestCaseDto == null) {
      throw new IllegalArgumentException("CreateTestCaseDto cannot be null");
    }

    if (createTestCaseDto.title().isEmpty() || createTestCaseDto.title().trim().isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }

    return mapToTestCase(createTestCaseDto);
  }

  private TestCase mapToTestCase(CreateTestCaseDto createTestCaseDto) {
    TestCase testCase =
        TestCase.builder()
            .title(createTestCaseDto.title())
            .description(createTestCaseDto.description())
            .status(createTestCaseDto.status() != null ? createTestCaseDto.status() : Status.OPEN)
            .reuqirementId(createTestCaseDto.requirementId())
            .build();
    return testCase;
  }
}
