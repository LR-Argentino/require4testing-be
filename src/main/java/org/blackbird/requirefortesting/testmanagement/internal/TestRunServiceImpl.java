package org.blackbird.requirefortesting.testmanagement.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.internal.validation.TestRunValidator;
import org.blackbird.requirefortesting.testmanagement.internal.validation.ValidationMessage;
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
    TestRunValidator.validateNotNull(testRunDto, ValidationMessage.NULL_TEST_RUN_DTO.name());
    TestRunValidator.validateForCreation(testRunDto);

    return testRunRepository.save(mapToTestRun(testRunDto));
  }

  @Override
  public TestRun update(Long testRunId, CreateTestRunDto testRunDto) {
    TestRunValidator.validateNotNull(testRunId, ValidationMessage.NULL_TEST_ID.name());
    TestRunValidator.validateNotNull(testRunDto, ValidationMessage.NULL_TEST_RUN_DTO.name());

    TestRun existingTestRun = findTestRunById(testRunId);
    updateTestRunFields(existingTestRun, testRunDto);

    return testRunRepository.save(existingTestRun);
  }

  @Override
  public void delete(Long testRunId) {
    TestRunValidator.validateNotNull(testRunId, ValidationMessage.NULL_TEST_ID.name());
    TestRun testRun = findTestRunById(testRunId);

    testRunRepository.delete(testRun);
  }

  private TestRun findTestRunById(Long testRunId) {
    return testRunRepository.findById(testRunId).orElseThrow(EntityNotFoundException::new);
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
