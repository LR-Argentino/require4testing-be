package org.blackbird.requirefortesting.testmanagement.internal;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.service.TestRunService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestRunServiceImpl implements TestRunService {

  private final TestRunRepository testRunRepository;
  private static final Integer BUFFER_FOR_CLOCK_SKEW = 30;

  @Override
  public TestRun create(CreateTestRunDto testRunDto) {
    if (testRunDto == null) {
      throw new IllegalArgumentException("Test run DTO cannot be null");
    }

    if (testRunDto.title() == null || testRunDto.title().isBlank()) {
      throw new IllegalArgumentException("Test run title cannot be null");
    }

    if (testRunDto.startDate() == null || testRunDto.endDate() == null) {
      throw new IllegalArgumentException("Test run start and end dates cannot be null");
    }

    LocalDateTime now = LocalDateTime.now();

    Boolean isStartDateInPast =
        testRunDto.startDate().isBefore(now.minusSeconds(BUFFER_FOR_CLOCK_SKEW));
    Boolean isStartDateAfterEndDate = testRunDto.startDate().isAfter(testRunDto.endDate());

    if (isStartDateInPast || isStartDateAfterEndDate) {
      throw new IllegalArgumentException("Test run start date cannot be in the past");
    }

    return testRunRepository.save(mapToTestRun(testRunDto));
  }

  private TestRun mapToTestRun(CreateTestRunDto testRunDto) {
    return TestRun.builder()
        .title(testRunDto.title())
        .startTime(testRunDto.startDate())
        .endTime(testRunDto.endDate())
        .build();
  }
}
