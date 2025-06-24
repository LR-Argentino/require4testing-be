package org.blackbird.requirefortesting.testmanagement.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.shared.JwtService;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.service.TestCaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

  private final TestCaseService testCaseService;
  private static final String AUTHORIZATION_HEADER = "Authorization";

  private final JwtService jwtUtil;

  @PostMapping
  public ResponseEntity<TestCase> createTestCase(
      @RequestHeader(AUTHORIZATION_HEADER) String authToken,
      @RequestBody CreateOrUpdateTestCaseDto createTestCaseDto) {
    Long userId = jwtUtil.extractUserId(authToken);
    TestCase testCase = testCaseService.createTestCase(createTestCaseDto, userId);
    return ResponseEntity.ok(testCase);
  }

  @PostMapping("/{id}")
  public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
    testCaseService.deleteTestCase(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<TestCase> updateTestCase(
      @PathVariable Long id, @RequestBody CreateOrUpdateTestCaseDto updateTestCaseDto) {
    TestCase updatedTestCase = testCaseService.updateTestCase(id, updateTestCaseDto);
    return ResponseEntity.ok(updatedTestCase);
  }

  @GetMapping
  public ResponseEntity<List<TestCase>> getAllTestCases() {

    List<TestCase> testCases = testCaseService.getAllTestCases();
    return ResponseEntity.ok(testCases);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TestCase> getTestCase(@PathVariable Long id) {
    TestCase testCase = testCaseService.getTestCase(id);
    return ResponseEntity.ok(testCase);
  }
}
