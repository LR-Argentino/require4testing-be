package org.blackbird.requirefortesting.testmanagement.internal;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.internal.validation.TestRunValidator;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.service.TestRunService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestRunServiceImpl implements TestRunService {

  private final TestRunRepository testRunRepository;

  @Override
  public TestRun create(CreateTestRunDto testRunDto) {
    TestRunValidator.validateNotNull(testRunDto, "Test run DTO cannot be null");
    TestRunValidator.validateForCreation(testRunDto);

    return testRunRepository.save(mapToTestRun(testRunDto));
  }

  @Override
  public TestRun update(Long testRunId, CreateTestRunDto testRunDto) {
    TestRunValidator.validateNotNull(testRunId, "Test run ID cannot be null");
    TestRunValidator.validateNotNull(testRunDto, "Test run DTO cannot be null");

    TestRun existingTestRun = findTestRunById(testRunId);
    updateTestRunFields(existingTestRun, testRunDto);

    return testRunRepository.save(existingTestRun);
  }

  private TestRun findTestRunById(Long testRunId) {
    return testRunRepository
        .findById(testRunId)
        .orElseThrow(
            () ->
                new IllegalArgumentException("Test run with ID " + testRunId + " does not exist"));
  }

  private void updateTestRunFields(TestRun existingTestRun, CreateTestRunDto testRunDto) {
    if (testRunDto.title() != null) {
      TestRunValidator.validateTitle(testRunDto.title());
      existingTestRun.setTitle(testRunDto.title());
    }

    if (testRunDto.startDate() != null) {
      TestRunValidator.validateStartDateForUpdate(existingTestRun, testRunDto);
      existingTestRun.setStartTime(testRunDto.startDate());
    }

    if (testRunDto.endDate() != null) {
      TestRunValidator.validateEndDate(existingTestRun, testRunDto);
      existingTestRun.setEndTime(testRunDto.endDate());
    }
  }

  private TestRun mapToTestRun(CreateTestRunDto testRunDto) {
    return TestRun.builder()
        .title(testRunDto.title())
        .startTime(testRunDto.startDate())
        .endTime(testRunDto.endDate())
        .build();
  }
}
