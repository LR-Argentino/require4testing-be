package org.blackbird.requirefortesting.testexecution.internal.repository;

import java.util.List;
import org.blackbird.requirefortesting.testexecution.model.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {

  List<TestExecution> findByTesterId(Long testerId);

  List<TestExecution> findByTestRunId(Long testRunId);

  TestExecution findByTestRunIdAndTestCaseIdAndTesterId(
      Long testRunId, Long testCaseId, Long testerId);
}
