package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import org.blackbird.requirefortesting.testmanagement.internal.TestRunServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestRunServiceAddToTestCaseTests {

  @Mock private TestCaseRepository testCaseRepository;
  @Mock private TestRunRepository testRunRepository;
  @InjectMocks private TestRunServiceImpl testRunService;

  @Test
  void test_addTestCaseToTestRunWithNullId_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> testRunService.addTestCase(null, null));
  }

  @Test
  void test_addTestCaseToTestRunWithNotFoundTestRun_shouldThrowException() {
    Long testRunId = 1L;
    Long testCaseId = 10L;

    when(testRunRepository.findById(testRunId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> testRunService.addTestCase(testRunId, testCaseId));
  }

  @Test
  void test_addTestCaseToTestRunNotFoundTestCase_shouldThrowException() {
    Long testRunId = 1L;
    Long testCaseId = 10L;

    when(testRunRepository.findById(testRunId)).thenReturn(Optional.of(new TestRun()));
    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> testRunService.addTestCase(testRunId, testCaseId));
  }

  @Test
  void test_addDuplicateTestCaseToTestRun_shouldThrowExceptionOnDuplicateEntry() {
    Long testRunId = 1L;
    Long testCaseId = 10L;
    Long createdById = 1L;

    TestRun testRun =
        TestRun.builder().title("Smoke Test Run").id(testRunId).createdBy(createdById).build();

    TestCase testCase = TestCase.builder().id(testCaseId).title("Smoke Test Case").build();
    testRun.setTestCases(Set.of(testCase));

    when(testRunRepository.findById(testRunId)).thenReturn(Optional.of(testRun));
    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

    assertThrows(
        IllegalStateException.class, () -> testRunService.addTestCase(testRunId, testCaseId));
  }

  @Test
  void test_addTestCaseToTestRun_shouldAddTestCaseToTestRun() {
    Long testRunId = 1L;
    Long testCaseId = 10L;
    Long createdById = 1L;

    TestRun testRun =
        TestRun.builder().title("Smoke Test Run").id(testRunId).createdBy(createdById).build();

    TestCase testCase = TestCase.builder().id(testCaseId).title("Smoke Test Case").build();

    when(testRunRepository.findById(testRunId)).thenReturn(Optional.of(testRun));
    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

    assertDoesNotThrow(() -> testRunService.addTestCase(testRunId, testCaseId));
  }
}
