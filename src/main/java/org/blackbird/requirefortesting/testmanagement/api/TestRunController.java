package org.blackbird.requirefortesting.testmanagement.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.service.TestRunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-runs")
@RequiredArgsConstructor
public class TestRunController {
  private final TestRunService testRunService;

  @PostMapping
  public ResponseEntity<TestRun> createTestRun(@RequestBody CreateTestRunDto testRunDto) {
    TestRun testRun = testRunService.create(testRunDto);
    return ResponseEntity.ok(testRun);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TestRun> updateTestRun(
      @PathVariable Long id, @RequestBody CreateTestRunDto testRunDto) {
    TestRun updatedTestRun = testRunService.update(id, testRunDto);
    return ResponseEntity.ok(updatedTestRun);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestRun> getTestRunById(@PathVariable Long id) {
    TestRun testRun = testRunService.getTestRunById(id);
    return ResponseEntity.ok(testRun);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteTestRun(@PathVariable Long id) {
    testRunService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<TestRun>> getAllTestRuns() {
    List<TestRun> testRuns = testRunService.getAllTestRuns();
    return ResponseEntity.ok(testRuns);
  }

  @PostMapping("/{testRunId}/test-cases/{testCaseId}")
  public ResponseEntity<Void> addTestCaseToTestRun(
      @PathVariable Long testRunId, @PathVariable Long testCaseId) {
    testRunService.addTestCase(testRunId, testCaseId);
    return ResponseEntity.noContent().build();
  }
}
