package org.blackbird.requirefortesting.requirements.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.blackbird.requirefortesting.requirements.model.CreateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequirementServiceTests {

  @InjectMocks private RequirementServiceImpl requirementService;

  @Test
  void test_createWithValidData_shouldCreateRequirement() {
    String title = "New Requirement";
    String description = "This is a test requirement.";
    Priority priority = Priority.HIGH;

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, priority);

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

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, priority);

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

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, priority);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementService.createRequirement(createRequirement);
        });
  }

  @Test
  void test_createWithValidData_statusShouldBeOpen() throws Exception {
    String title = "Valid Requirement";
    String description = "This requirement is valid and should be created.";
    Priority priority = Priority.MEDIUM;

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, priority);

    Requirement requirement = requirementService.createRequirement(createRequirement);

    assertThat(requirement.getStatus()).isEqualTo(Status.OPEN);
  }

  @Test
  void test_createWithPriorityNull_priorityShouldBeLow() throws Exception {
    String title = "Valid Requirement";
    String description = "This requirement is valid and should be created.";

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, null);

    Requirement requirement = requirementService.createRequirement(createRequirement);

    assertThat(requirement.getPriority()).isEqualTo(Priority.LOW);
  }
}
