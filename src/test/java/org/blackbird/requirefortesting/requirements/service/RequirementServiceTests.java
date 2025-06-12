package org.blackbird.requirefortesting.requirements.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.repository.RequirementRepository;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequirementServiceTests {

  @Mock private RequirementRepository requirementRepository;
  @InjectMocks private RequirementServiceImpl requirementService;

  @Test
  void test_createWithValidData_shouldCreateRequirement() {
    String title = "New Requirement";
    String description = "This is a test requirement.";
    Priority priority = Priority.HIGH;

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    assertDoesNotThrow(
        () -> {
          requirementService.createRequirement(createRequirement);
        });
  }

  @Test
  void test_createWithInvalidTitle_shouldThrowException() {
    String title = "";
    String description = "This is a test requirement.";
    Priority priority = Priority.HIGH;

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.createRequirement(createRequirement);
        });
  }

  @Test
  void test_createWithSpecialCharactersInTitle_shouldThrowException() {
    String title = "Requirement @#$%";
    String description = "This is a test requirement with special characters.";
    Priority priority = Priority.HIGH;

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.createRequirement(createRequirement);
        });
  }

  @Test
  void test_createWithValidData_statusShouldBeOpen() {
    String title = "Valid Requirement";
    String description = "This requirement is valid and should be created.";
    Priority priority = Priority.MEDIUM;

    Requirement expectedRequirement =
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(Status.OPEN)
            .build();

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);

    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getStatus()).isEqualTo(Status.OPEN);
  }

  @Test
  void test_createWithPriorityNull_priorityShouldBeLow() {
    String title = "Valid Requirement";
    String description = "This requirement is valid and should be created.";
    Priority priority = Priority.LOW;

    Requirement expectedRequirement =
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(Status.OPEN)
            .build();

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, null);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);
    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getPriority()).isEqualTo(Priority.LOW);
  }

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
