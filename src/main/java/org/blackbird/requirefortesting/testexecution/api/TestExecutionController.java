package org.blackbird.requirefortesting.testexecution.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.JwtService;
import org.blackbird.requirefortesting.testexecution.model.TestExecution;
import org.blackbird.requirefortesting.testexecution.model.UpdateTestResultDto;
import org.blackbird.requirefortesting.testexecution.service.TestExecutionService;
import org.blackbird.requirefortesting.testmanagement.model.TestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-executions")
@RequiredArgsConstructor
public class TestExecutionController {
  private final TestExecutionService executionService;
  private final JwtService jwtService;

  @PreAuthorize("hasRole('TEST_MANAGER')")
  @PostMapping("/runs/{testRunId}/cases/{testCaseId}/assign/{testerId}")
  public ResponseEntity<TestExecution> assignTestCase(
      @PathVariable Long testRunId, @PathVariable Long testCaseId, @PathVariable Long testerId) {
    TestExecution execution =
        executionService.assignTestCaseToTester(testRunId, testCaseId, testerId);
    return ResponseEntity.ok(execution);
  }

  @PreAuthorize("hasRole('TESTER')")
  @PutMapping("/{executionId}/result")
  public ResponseEntity<TestExecution> submitResult(
      @PathVariable Long executionId,
      @RequestBody UpdateTestResultDto updateDto,
      @RequestHeader("Authorization") String authToken) {
    Long testerId = jwtService.extractUserId(authToken);
    TestResult result = updateDto.testResult();
    String comment = updateDto.comment();
    TestExecution execution =
        executionService.submitTestResult(executionId, testerId, result, comment);
    return ResponseEntity.ok(execution);
  }

  @PreAuthorize("hasRole('TESTER')")
  @GetMapping("/assigned")
  public ResponseEntity<List<TestExecution>> getAssignedExecutions(
      @RequestHeader("Authorization") String authToken) {
    Long testerId = jwtService.extractUserId(authToken);
    List<TestExecution> executions = executionService.getExecutionsForTester(testerId);
    return ResponseEntity.ok(executions);
  }
}
