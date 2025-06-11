package org.blackbird.requirefortesting.requirements.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.blackbird.requirefortesting.requirements.model.dto.CreateRequirementDto;
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
    String priority = "High";

    CreateRequirementDto createRequirement = new CreateRequirementDto(title, description, priority);

    assertDoesNotThrow(
        () -> {
          requirementService.createRequirement(createRequirement);
        });
  }
}
