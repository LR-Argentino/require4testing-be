package org.blackbird.requirefortesting.requirements.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.blackbird.requirefortesting.TestPostgreSQLContainer;
import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class RequirementRepositoryTest {

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    TestPostgreSQLContainer.configureProperties(registry);
  }

  @Autowired private RequirementRepository requirementRepository;

  private Requirement testRequirement1, testRequirement2, testRequirement3;

  @BeforeEach
  void setUp() {
    requirementRepository.deleteAll();

    testRequirement1 =
        createRequirement("Test Requirement 1", "Description 1", Priority.HIGH, Status.OPEN);
    testRequirement2 =
        createRequirement(
            "Test Requirement 2", "Description 2", Priority.MEDIUM, Status.IN_PROGRESS);
    testRequirement3 =
        createRequirement("Test Requirement 3", "Description 3", Priority.LOW, Status.CLOSED);
  }

  private Requirement createRequirement(
      String title, String description, Priority priority, Status status) {
    return requirementRepository.save(
        Requirement.builder()
            .title(title)
            .description(description)
            .priority(priority)
            .status(status)
            .build());
  }

  @Test
  @Transactional
  void test_save_withValidRequirement_shouldSaveRequirement() {
    Requirement requirement =
        Requirement.builder()
            .title("Valid Test Requirement")
            .description("This is a valid test requirement")
            .priority(Priority.LOW)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);

    assertNotNull(savedRequirement.getId());
    assertNotNull(savedRequirement.getCreatedAt());
    assertNotNull(savedRequirement.getUpdatedAt());
    assertEquals("Valid Test Requirement", savedRequirement.getTitle());
    assertEquals("This is a valid test requirement", savedRequirement.getDescription());
    assertEquals(Priority.LOW, savedRequirement.getPriority());
    assertEquals(Status.OPEN, savedRequirement.getStatus());
  }

  @Test
  @Transactional
  void test_save_withNullTitle_shouldThrowException() {
    Requirement requirement =
        Requirement.builder()
            .description("This is a test requirement without title")
            .priority(Priority.LOW)
            .build();

    assertThrows(Exception.class, () -> requirementRepository.save(requirement));
  }

  @Test
  @Transactional
  void test_save_withNullPriority_shouldThrowException() {
    Requirement requirement =
        Requirement.builder()
            .title("Test Requirement")
            .description("This is a test requirement without priority")
            .build();

    assertThrows(Exception.class, () -> requirementRepository.save(requirement));
  }

  @Test
  @Transactional
  void test_findById_withExistingId_shouldReturnRequirement() {
    Optional<Requirement> foundRequirement =
        requirementRepository.findById(testRequirement1.getId());

    assertTrue(foundRequirement.isPresent());
    assertEquals(testRequirement1.getTitle(), foundRequirement.get().getTitle());
    assertEquals(testRequirement1.getDescription(), foundRequirement.get().getDescription());
    assertEquals(testRequirement1.getPriority(), foundRequirement.get().getPriority());
  }

  @Test
  @Transactional
  void test_findById_withNonExistentId_shouldReturnEmpty() {
    Optional<Requirement> foundRequirement = requirementRepository.findById(999L);

    assertFalse(foundRequirement.isPresent());
  }

  @Test
  @Transactional
  void test_findById_withNegativeId_shouldReturnEmpty() {
    Optional<Requirement> foundRequirement = requirementRepository.findById(-1L);

    assertFalse(foundRequirement.isPresent());
  }

  @Test
  @Transactional
  void test_findById_withZeroId_shouldReturnEmpty() {
    Optional<Requirement> foundRequirement = requirementRepository.findById(0L);

    assertFalse(foundRequirement.isPresent());
  }

  @Test
  @Transactional
  void test_findAll_shouldReturnAllRequirements() {
    List<Requirement> allRequirements = requirementRepository.findAll();

    assertEquals(3, allRequirements.size());
    assertTrue(allRequirements.stream().anyMatch(r -> r.getTitle().equals("Test Requirement 1")));
    assertTrue(allRequirements.stream().anyMatch(r -> r.getTitle().equals("Test Requirement 2")));
    assertTrue(allRequirements.stream().anyMatch(r -> r.getTitle().equals("Test Requirement 3")));
  }

  @Test
  @Transactional
  void test_findAll_withPagination_shouldReturnPagedResults() {
    Pageable pageable = PageRequest.of(0, 2, Sort.by("title"));
    Page<Requirement> requirementPage = requirementRepository.findAll(pageable);

    assertEquals(2, requirementPage.getContent().size());
    assertEquals(3, requirementPage.getTotalElements());
    assertEquals(2, requirementPage.getTotalPages());
    assertTrue(requirementPage.hasNext());
    assertFalse(requirementPage.hasPrevious());
  }

  @Test
  @Transactional
  void test_delete_withExistingRequirement_shouldRemoveRequirement() {
    Long requirementId = testRequirement1.getId();

    requirementRepository.delete(testRequirement1);

    Optional<Requirement> deletedRequirement = requirementRepository.findById(requirementId);
    assertFalse(deletedRequirement.isPresent());

    List<Requirement> remainingRequirements = requirementRepository.findAll();
    assertEquals(2, remainingRequirements.size());
  }

  @Test
  @Transactional
  void test_deleteById_withExistingId_shouldRemoveRequirement() {
    Long requirementId = testRequirement1.getId();

    requirementRepository.deleteById(requirementId);

    Optional<Requirement> deletedRequirement = requirementRepository.findById(requirementId);
    assertFalse(deletedRequirement.isPresent());

    List<Requirement> remainingRequirements = requirementRepository.findAll();
    assertEquals(2, remainingRequirements.size());
  }

  @Test
  @Transactional
  void test_deleteById_withNonExistentId_shouldNotThrowException() {
    int initialCount = requirementRepository.findAll().size();

    assertDoesNotThrow(() -> requirementRepository.deleteById(999L));

    List<Requirement> remainingRequirements = requirementRepository.findAll();
    assertEquals(initialCount, remainingRequirements.size());
  }

  @Test
  @Transactional
  void test_count_shouldReturnCorrectCount() {
    long count = requirementRepository.count();

    assertEquals(3, count);
  }

  @Test
  @Transactional
  void test_existsById_withExistingId_shouldReturnTrue() {
    boolean exists = requirementRepository.existsById(testRequirement1.getId());

    assertTrue(exists);
  }

  @Test
  @Transactional
  void test_existsById_withNonExistentId_shouldReturnFalse() {
    boolean exists = requirementRepository.existsById(999L);

    assertFalse(exists);
  }

  @Test
  @Transactional
  void test_save_withNullDescription_shouldSaveSuccessfully() {
    Requirement requirement =
        Requirement.builder()
            .title("Test Requirement")
            .description(null)
            .priority(Priority.LOW)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);

    assertNotNull(savedRequirement.getId());
    assertNull(savedRequirement.getDescription());
  }

  @Test
  @Transactional
  void test_save_withEmptyDescription_shouldSaveSuccessfully() {
    Requirement requirement =
        Requirement.builder()
            .title("Test Requirement")
            .description("")
            .priority(Priority.LOW)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);

    assertNotNull(savedRequirement.getId());
    assertEquals("", savedRequirement.getDescription());
  }

  @Test
  @Transactional
  void test_save_withUnicodeCharacters_shouldHandleCorrectly() {
    String unicodeTitle = "Test 测试 テスト 🚀";
    String unicodeDescription = "Description with unicode: αβγ δεζ ηθι";

    Requirement requirement =
        Requirement.builder()
            .title(unicodeTitle)
            .description(unicodeDescription)
            .priority(Priority.HIGH)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);

    assertNotNull(savedRequirement.getId());
    assertEquals(unicodeTitle, savedRequirement.getTitle());
    assertEquals(unicodeDescription, savedRequirement.getDescription());
  }

  @Test
  @Transactional
  void test_save_withAllPriorityValues_shouldSaveCorrectly() {
    for (Priority priority : Priority.values()) {
      Requirement requirement =
          Requirement.builder()
              .title("Test Requirement " + priority.name())
              .description("Test for priority " + priority.name())
              .priority(priority)
              .build();

      Requirement savedRequirement = requirementRepository.save(requirement);

      assertNotNull(savedRequirement.getId());
      assertEquals(priority, savedRequirement.getPriority());
    }
  }

  @Test
  @Transactional
  void test_save_withAllStatusValues_shouldSaveCorrectly() {
    for (Status status : Status.values()) {
      Requirement requirement =
          Requirement.builder()
              .title("Test Requirement " + status.name())
              .description("Test for status " + status.name())
              .priority(Priority.MEDIUM)
              .status(status)
              .build();

      Requirement savedRequirement = requirementRepository.save(requirement);

      assertNotNull(savedRequirement.getId());
      assertEquals(status, savedRequirement.getStatus());
    }
  }

  @Test
  @Transactional
  void test_save_withDefaultStatus_shouldBeOpen() {
    Requirement requirement =
        Requirement.builder()
            .title("Test Requirement")
            .description("Test requirement without explicit status")
            .priority(Priority.LOW)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);

    assertEquals(Status.OPEN, savedRequirement.getStatus());
  }

  @Test
  @Transactional
  void test_update_shouldUpdateTimestamp() {
    Requirement savedRequirement =
        requirementRepository.save(
            Requirement.builder()
                .title("Original Title")
                .description("Original Description")
                .priority(Priority.LOW)
                .build());

    LocalDateTime originalCreatedAt = savedRequirement.getCreatedAt();
    LocalDateTime originalUpdatedAt = savedRequirement.getUpdatedAt();

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    savedRequirement.setTitle("Updated Title");
    Requirement updatedRequirement = requirementRepository.save(savedRequirement);
    requirementRepository.flush();

    assertEquals(originalCreatedAt, updatedRequirement.getCreatedAt());
    assertTrue(updatedRequirement.getUpdatedAt().isAfter(originalUpdatedAt));
  }
}
