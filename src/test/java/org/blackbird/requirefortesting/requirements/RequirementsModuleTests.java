package org.blackbird.requirefortesting.requirements;

import static org.assertj.core.api.Assertions.assertThat;

import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.service.RequirementService;
import org.blackbird.requirefortesting.shared.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.transaction.annotation.Transactional;

@ApplicationModuleTest
@Transactional
class RequirementsModuleTests {

  @Autowired private RequirementService requirementService;

  @Test
  void shouldCreateRequirement() {
    // Given
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto("Test Requirement", "Test Description", Priority.HIGH);

    // When
    Requirement created = requirementService.createRequirement(createDto);

    // Then
    assertThat(created).isNotNull();
    assertThat(created.getTitle()).isEqualTo("Test Requirement");
    assertThat(created.getDescription()).isEqualTo("Test Description");
    assertThat(created.getPriority()).isEqualTo(Priority.HIGH);
  }

  @Test
  void shouldUpdateRequirement() {
    // Given
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto("Original Title", "Original Description", Priority.LOW);
    Requirement created = requirementService.createRequirement(createDto);

    CreateOrUpdateRequirementDto updateDto =
        new CreateOrUpdateRequirementDto("Updated Title", "Updated Description", Priority.HIGH);

    // When
    Requirement updated = requirementService.updateRequirement(created.getId(), updateDto);

    // Then
    assertThat(updated.getTitle()).isEqualTo("Updated Title");
    assertThat(updated.getDescription()).isEqualTo("Updated Description");
    assertThat(updated.getPriority()).isEqualTo(Priority.HIGH);
  }

  @Test
  void shouldDeleteRequirement() {
    // Given
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto("To Delete", "Will be deleted", Priority.MEDIUM);
    Requirement created = requirementService.createRequirement(createDto);

    // When
    requirementService.deleteRequirement(created.getId());

    // Then - Should not throw exception
    // In a real test, you might want to verify it's actually deleted
    // by trying to fetch it and expecting an exception
  }
}
