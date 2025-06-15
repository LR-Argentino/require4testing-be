package org.blackbird.requirefortesting.requirements.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.blackbird.requirefortesting.requirements.internal.RequirementServiceImpl;
import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequirementServiceUpdateTests {

  @Mock private RequirementRepository requirementRepository;
  @InjectMocks private RequirementServiceImpl requirementService;

  @Test
  void test_updateWithValidData_shouldUpdateRequirement() {
    Long id = 1L;
    String title = "Updated Requirement";
    String description = "This is an updated test requirement.";
    Priority priority = Priority.MEDIUM;

    CreateOrUpdateRequirementDto updateRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Old Title")
            .description("Old Description")
            .priority(Priority.LOW)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));

    assertDoesNotThrow(
        () -> {
          requirementService.updateRequirement(id, updateRequirement);
        });
  }

  @Test
  void test_updateWithInvalidId_shouldThrowException() {
    Long id = 999L; // Assuming this ID does not exist
    String title = "Updated Requirement";
    String description = "This is an updated test requirement.";
    Priority priority = Priority.MEDIUM;

    CreateOrUpdateRequirementDto updateRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    when(requirementRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.updateRequirement(id, updateRequirement);
        });
  }

  @Test
  void test_updateWithInvalidData_shouldThrowException() {
    Long id = 1L;
    String title = ""; // Invalid title
    String description = "This is an updated test requirement.";
    Priority priority = Priority.MEDIUM;

    CreateOrUpdateRequirementDto updateRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Old Title")
            .description("Old Description")
            .priority(Priority.LOW)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.updateRequirement(id, updateRequirement);
        });
  }

  @Test
  void test_updateOnStatusInProgress_shouldThrowException() {
    Long id = 1L;
    String title = "Updated Requirement";
    String description = "This is an updated test requirement.";
    Priority priority = Priority.MEDIUM;

    CreateOrUpdateRequirementDto updateRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Old Title")
            .description("Old Description")
            .priority(Priority.LOW)
            .status(Status.IN_PROGRESS)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));

    assertThrows(
        IllegalStateException.class,
        () -> {
          requirementService.updateRequirement(id, updateRequirement);
        });
  }

  @Test
  void test_updateOnStatusOpen_shouldUpdateAndReturnedRequirement() {
    Long id = 1L;
    String title = "Updated Requirement";
    String description = "This is an updated test requirement.";
    Priority priority = Priority.MEDIUM;

    CreateOrUpdateRequirementDto updateRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Old Title")
            .description("Old Description")
            .priority(Priority.LOW)
            .status(Status.OPEN)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));

    Requirement updatedRequirement = requirementService.updateRequirement(id, updateRequirement);

    assertThat(updatedRequirement.getTitle()).isEqualTo(title);
    assertThat(updatedRequirement.getDescription()).isEqualTo(description);
    assertThat(updatedRequirement.getPriority()).isEqualTo(priority);
  }

  @Test
  void test_deleteWithInvalidId_shouldThrowException() {
    Long id = 999L;

    when(requirementRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.deleteRequirement(id);
        });
  }

  @Test
  void test_deleteWithValidId_shouldDeleteRequirement() {
    Long id = 1L;

    Requirement existingRequirement =
        Requirement.builder()
            .id(id)
            .title("Old Title")
            .description("Old Description")
            .priority(Priority.LOW)
            .build();

    when(requirementRepository.findById(id)).thenReturn(Optional.of(existingRequirement));

    assertDoesNotThrow(
        () -> {
          requirementService.deleteRequirement(id);
        });
  }
}
