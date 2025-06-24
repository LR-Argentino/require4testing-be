package org.blackbird.requirefortesting.requirements.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.blackbird.requirefortesting.requirements.internal.RequirementServiceImpl;
import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequirementServiceDeleteTests {

  @Mock private RequirementRepository requirementRepository;
  @InjectMocks private RequirementServiceImpl requirementService;

  @Test
  void test_deleteWithValidId_shouldDeleteRequirement() {
    Long id = 1L;
    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Test Requirement")
            .description("Test Description")
            .priority(Priority.LOW)
            .status(Status.OPEN)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));
    doNothing().when(requirementRepository).delete(existingRequirement);

    assertDoesNotThrow(() -> requirementService.deleteRequirement(id));
    verify(requirementRepository, times(1)).findById(id);
    verify(requirementRepository, times(1)).delete(existingRequirement);
  }

  @Test
  void test_deleteWithNullId_shouldThrowException() {
    Long id = null;

    assertThrows(EntityNotFoundException.class, () -> requirementService.deleteRequirement(id));
  }

  @Test
  void test_deleteRequirementWithInProgressStatus_shouldDeleteSuccessfully() {
    Long id = 1L;
    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Test Requirement")
            .description("Test Description")
            .priority(Priority.MEDIUM)
            .status(Status.IN_PROGRESS)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));
    doNothing().when(requirementRepository).delete(existingRequirement);

    assertDoesNotThrow(() -> requirementService.deleteRequirement(id));
    verify(requirementRepository, times(1)).delete(existingRequirement);
  }

  @Test
  void test_deleteRequirementWithCompletedStatus_shouldDeleteSuccessfully() {
    Long id = 1L;
    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Test Requirement")
            .description("Test Description")
            .priority(Priority.HIGH)
            .status(Status.CLOSED)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));
    doNothing().when(requirementRepository).delete(existingRequirement);

    assertDoesNotThrow(() -> requirementService.deleteRequirement(id));
    verify(requirementRepository, times(1)).delete(existingRequirement);
  }

  @Test
  void test_deleteRequirementWithDifferentPriorities_shouldDeleteSuccessfully() {
    Long id = 1L;
    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("High Priority Requirement")
            .description("Critical requirement")
            .priority(Priority.HIGH)
            .status(Status.OPEN)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));
    doNothing().when(requirementRepository).delete(existingRequirement);

    assertDoesNotThrow(() -> requirementService.deleteRequirement(id));
    verify(requirementRepository, times(1)).delete(existingRequirement);
  }
}
