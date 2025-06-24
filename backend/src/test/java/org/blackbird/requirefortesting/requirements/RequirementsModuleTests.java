package org.blackbird.requirefortesting.requirements;

import static org.assertj.core.api.Assertions.assertThat;

import org.blackbird.requirefortesting.TestPostgreSQLContainer;
import org.blackbird.requirefortesting.requirements.internal.RequirementServiceImpl;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Transactional
@Testcontainers
@SpringBootTest
class RequirementsModuleTests {

  @Autowired private RequirementServiceImpl requirementService;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    TestPostgreSQLContainer.configureProperties(registry); // ← Shared Container
  }

  @Test
  void shouldCreateRequirement() {
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto(
            "Test Requirement", "Test Description", Priority.HIGH, null);

    Requirement created = requirementService.createRequirement(createDto, 1L);

    assertThat(created).isNotNull();
    assertThat(created.getTitle()).isEqualTo("Test Requirement");
    assertThat(created.getDescription()).isEqualTo("Test Description");
    assertThat(created.getPriority()).isEqualTo(Priority.HIGH);
  }

  @Test
  void shouldUpdateRequirement() {
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto(
            "Original Title", "Original Description", Priority.LOW, null);
    Requirement created = requirementService.createRequirement(createDto, 1L);

    CreateOrUpdateRequirementDto updateDto =
        new CreateOrUpdateRequirementDto(
            "Updated Title", "Updated Description", Priority.HIGH, null);

    Requirement updated = requirementService.updateRequirement(created.getId(), updateDto);

    assertThat(updated.getTitle()).isEqualTo("Updated Title");
    assertThat(updated.getDescription()).isEqualTo("Updated Description");
    assertThat(updated.getPriority()).isEqualTo(Priority.HIGH);
  }

  @Test
  void shouldDeleteRequirement() {
    CreateOrUpdateRequirementDto createDto =
        new CreateOrUpdateRequirementDto("To Delete", "Will be deleted", Priority.MEDIUM, null);
    Requirement created = requirementService.createRequirement(createDto, 1L);

    requirementService.deleteRequirement(created.getId());
  }
}
