package org.blackbird.requirefortesting.testmanagement.service;

import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;

public interface TestRunService {
  TestRun create(CreateTestRunDto testRunDto);
}
