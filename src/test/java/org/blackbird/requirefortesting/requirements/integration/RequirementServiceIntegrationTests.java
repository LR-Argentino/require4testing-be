package org.blackbird.requirefortesting.requirements.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.blackbird.requirefortesting.TestPostgreSQLContainer;
import org.blackbird.requirefortesting.requirements.internal.RequirementServiceImpl;
import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RequirementServiceIntegrationTests {
  @Autowired private RequirementRepository requirementRepository;
  @Autowired private RequirementServiceImpl requirementServiceImpl;

  private Requirement requirement1, requirement2, requirement3;

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    TestPostgreSQLContainer.configureProperties(registry);
  }

  @BeforeEach
  void setUp() {
    requirement1 = createRequirement("Requirement 1", "Description 1", Priority.HIGH);
    requirement2 = createRequirement("Requirement 2", "Description 2", Priority.MEDIUM);
    requirement3 = createRequirement("Requirement 3", "Description 3", Priority.LOW);
  }

  @Test
  @Transactional
  void test_saveRequirement_shouldPersistRequirement() {
    String title = "Sample Requirement";
    String description = "This is a sample requirement";

    CreateOrUpdateRequirementDto requirement =
        new CreateOrUpdateRequirementDto(title, description, Priority.HIGH, null);

    Requirement createdRequirement = requirementServiceImpl.createRequirement(requirement);

    assertNotNull(createdRequirement.getId(), "Saved requirement should have an ID");
    assertEquals(
        "Sample Requirement", createdRequirement.getTitle(), "Requirement title should match");
    assertEquals(
        Priority.HIGH, createdRequirement.getPriority(), "Requirement priority should match");
    assertEquals(
        Status.OPEN,
        createdRequirement.getStatus(),
        "Requirement status should be OPEN by default");
  }

  @Test
  @Transactional
  void test_fetchRequirementById_shouldReturnRequirement() {
    Requirement requirement =
        requirementRepository
            .findById(requirement1.getId())
            .orElseThrow(
                () ->
                    new AssertionError(
                        "Requirement with ID " + requirement1.getId() + " not found"));

    assertEquals(requirement1.getId(), requirement.getId(), "Requirement ID should match");
    assertEquals("Requirement 1", requirement.getTitle(), "Requirement title should match");
    assertEquals(
        "Description 1", requirement.getDescription(), "Requirement description should match");
    assertEquals(Priority.HIGH, requirement.getPriority(), "Requirement priority should match");
  }

  @Test
  @Transactional
  void test_updateRequirement_shouldUpdateRequirement() {
    String updatedTitle = "Updated Requirement";
    String updatedDescription = "Updated description";
    CreateOrUpdateRequirementDto updateDto =
        new CreateOrUpdateRequirementDto(updatedTitle, updatedDescription, Priority.LOW, null);

    Requirement updatedRequirement =
        requirementServiceImpl.updateRequirement(requirement1.getId(), updateDto);

    assertEquals(updatedTitle, updatedRequirement.getTitle(), "Title should be updated");
    assertEquals(
        updatedDescription, updatedRequirement.getDescription(), "Description should be updated");
    assertEquals(Priority.LOW, updatedRequirement.getPriority(), "Priority should be updated");
  }

  @Test
  @Transactional
  void test_deleteRequirement_shouldRemoveRequirement() {
    Long requirementId = requirement1.getId();

    requirementServiceImpl.deleteRequirement(requirementId);

    Optional<Requirement> deletedRequirement = requirementRepository.findById(requirementId);
    assertTrue(deletedRequirement.isEmpty(), "Requirement should be deleted");
  }

  // Edge Case Tests
  @Test
  @Transactional
  void test_findById_withNonExistentId_shouldReturnEmpty() {
    Long nonExistentId = 99999L;

    Optional<Requirement> result = requirementRepository.findById(nonExistentId);

    assertTrue(result.isEmpty(), "Should return empty for non-existent ID");
  }

  @Test
  @Transactional
  void test_findById_withNegativeId_shouldReturnEmpty() {
    Long negativeId = -1L;

    Optional<Requirement> result = requirementRepository.findById(negativeId);

    assertTrue(result.isEmpty(), "Should return empty for negative ID");
  }

  @Test
  @Transactional
  void test_createRequirement_withNullTitle_shouldThrowException() {
    CreateOrUpdateRequirementDto invalidRequirement =
        new CreateOrUpdateRequirementDto(null, "Description", Priority.HIGH, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.createRequirement(invalidRequirement);
        },
        "Should throw exception for null title");
  }

  @Test
  @Transactional
  void test_createRequirement_withEmptyTitle_shouldThrowException() {
    CreateOrUpdateRequirementDto invalidRequirement =
        new CreateOrUpdateRequirementDto("", "Description", Priority.HIGH, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.createRequirement(invalidRequirement);
        },
        "Should throw exception for empty title");
  }

  @Test
  @Transactional
  void test_createRequirement_withWhitespaceOnlyTitle_shouldThrowException() {
    CreateOrUpdateRequirementDto invalidRequirement =
        new CreateOrUpdateRequirementDto("   ", "Description", Priority.HIGH, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.createRequirement(invalidRequirement);
        },
        "Should throw exception for whitespace-only title");
  }

  @Test
  @Transactional
  void test_createRequirement_withSpecialCharacters_shouldThrowException() {
    CreateOrUpdateRequirementDto invalidRequirement =
        new CreateOrUpdateRequirementDto("Title@#$", "Description", Priority.HIGH, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.createRequirement(invalidRequirement);
        },
        "Should throw exception for title with special characters");
  }

  @Test
  @Transactional
  void test_createRequirement_withNullData_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.createRequirement(null);
        },
        "Should throw exception for null requirement data");
  }

  @Test
  @Transactional
  void test_updateRequirement_withNonExistentId_shouldThrowException() {
    Long nonExistentId = 99999L;
    CreateOrUpdateRequirementDto updateDto =
        new CreateOrUpdateRequirementDto("Valid Title", "Valid Description", Priority.HIGH, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.updateRequirement(nonExistentId, updateDto);
        },
        "Should throw exception for non-existent requirement ID");
  }

  @Test
  @Transactional
  void test_updateRequirement_withNullData_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.updateRequirement(requirement1.getId(), null);
        },
        "Should throw exception for null update data");
  }

  @Test
  @Transactional
  void test_deleteRequirement_withNonExistentId_shouldThrowException() {
    Long nonExistentId = 99999L;

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          requirementServiceImpl.deleteRequirement(nonExistentId);
        },
        "Should throw exception for non-existent requirement ID");
  }

  private Requirement createRequirement(String title, String description, Priority priority) {
    CreateOrUpdateRequirementDto requirementDto =
        new CreateOrUpdateRequirementDto(title, description, priority, null);
    return requirementServiceImpl.createRequirement(requirementDto);
  }
}
