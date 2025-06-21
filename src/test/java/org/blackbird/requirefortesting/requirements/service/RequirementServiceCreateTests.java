package org.blackbird.requirefortesting.requirements.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class RequirementServiceCreateTests {

  @Mock private RequirementRepository requirementRepository;
  @InjectMocks private RequirementServiceImpl requirementService;

  @Test
  void test_createWithValidData_shouldCreateRequirement() {
    String title = "New Requirement";
    String description = "This is a test requirement.";
    Priority priority = Priority.HIGH;

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority, null);

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
        new CreateOrUpdateRequirementDto(title, description, priority, null);

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
        new CreateOrUpdateRequirementDto(title, description, priority, null);

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
        new CreateOrUpdateRequirementDto(title, description, priority, null);
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
        new CreateOrUpdateRequirementDto(title, description, null, null);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);
    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getPriority()).isEqualTo(Priority.LOW);
  }

  @Test
  void test_createWithNullData_shouldThrowException() {
    CreateOrUpdateRequirementDto createRequirement = null;

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> requirementService.createRequirement(createRequirement));

    assertThat(exception.getMessage()).isEqualTo("Requirement data cannot be null");
  }

  @Test
  void test_createWithBlankTitle_shouldThrowException() {
    String title = "   ";
    String description = "This is a test requirement.";
    Priority priority = Priority.HIGH;

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> requirementService.createRequirement(createRequirement));
  }

  @Test
  void test_createWithNumbersInTitle_shouldCreateSuccessfully() {
    String title = "Requirement 123";
    String description = "This requirement contains numbers.";
    Priority priority = Priority.MEDIUM;

    Requirement expectedRequirement =
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(Status.OPEN)
            .build();

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority, null);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);

    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getTitle()).isEqualTo(title);
    assertThat(result.getDescription()).isEqualTo(description);
    assertThat(result.getPriority()).isEqualTo(priority);
    verify(requirementRepository, times(1))
        .save(org.mockito.ArgumentMatchers.any(Requirement.class));
  }

  @Test
  void test_createWithEmptyDescription_shouldCreateSuccessfully() {
    String title = "Valid Title";
    String description = "";
    Priority priority = Priority.LOW;

    Requirement expectedRequirement =
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(Status.OPEN)
            .build();

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority, null);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);

    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getDescription()).isEqualTo(description);
    verify(requirementRepository, times(1))
        .save(org.mockito.ArgumentMatchers.any(Requirement.class));
  }

  @Test
  void test_createWithAllPriorityLevels_shouldCreateSuccessfully() {
    String title = "High Priority Requirement";
    String description = "This is a high priority requirement.";
    Priority priority = Priority.HIGH;

    Requirement expectedRequirement =
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(Status.OPEN)
            .build();

    CreateOrUpdateRequirementDto createRequirement =
        new CreateOrUpdateRequirementDto(title, description, priority, null);
    when(requirementRepository.save(org.mockito.ArgumentMatchers.any(Requirement.class)))
        .thenReturn(expectedRequirement);

    Requirement result = requirementService.createRequirement(createRequirement);

    assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    verify(requirementRepository, times(1))
        .save(org.mockito.ArgumentMatchers.any(Requirement.class));
  }
}
