package org.blackbird.requirefortesting.testmanagement.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.TestCaseServiceImpl;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceDeleteTests {

  @Mock private TestCaseRepository testCaseRepository;
  @InjectMocks private TestCaseServiceImpl testManagementService;

  @Test
  void test_deleteTestCase_withNullId_throwsException() {
    Long testCaseId = null;

    assertThrows(
        IllegalArgumentException.class,
        () -> testManagementService.deleteTestCase(testCaseId),
        "Test case ID cannot be null");
  }

  @Test
  void test_deleteTestCase_withNegativeId_throwsException() {
    Long testCaseId = -1L;

    assertThrows(
        IllegalArgumentException.class,
        () -> testManagementService.deleteTestCase(testCaseId),
        "Test case ID cannot be negative");
  }

  @Test
  void test_deleteTestCase_withValidIdButNotFound_throwsException() {
    Long testCaseId = 199L;

    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class,
        () -> testManagementService.deleteTestCase(testCaseId),
        "Test case not found with id: " + testCaseId);
  }

  @Test
  void test_deleteTestCase_withValidId_deletesSuccessfully() {
    Long testCaseId = 19L;
    TestCase testCase =
        TestCase.builder()
            .id(testCaseId)
            .title("Test Title")
            .description("Test Description")
            .status(Status.OPEN)
            .build();

    when(testCaseRepository.findById(testCaseId)).thenReturn(Optional.of(testCase));

    testManagementService.deleteTestCase(testCaseId);

    verify(testCaseRepository).findById(testCaseId);
    verify(testCaseRepository).delete(testCase);
  }
}
