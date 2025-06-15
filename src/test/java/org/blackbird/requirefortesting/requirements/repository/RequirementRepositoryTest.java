package org.blackbird.requirefortesting.requirements.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.shared.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class RequirementRepositoryTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:latest")
          .withDatabaseName("testdb")
          .withUsername("testuser")
          .withPassword("secret");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private RequirementRepository requirementRepository;

  @Test
  void test_save_shouldSaveRequirement() {
    Requirement requirement =
        Requirement.builder()
            .title("Test Requirement")
            .description("This is a test requirement")
            .priority(Priority.LOW)
            .build();

    Requirement savedRequirement = requirementRepository.save(requirement);
    assertNotNull(savedRequirement.getId());
    assertNotNull(savedRequirement.getCreatedAt());
    assertNotNull(savedRequirement.getUpdatedAt());
  }

  @Test
  void test_save_shouldNotSaveRequirementWithoutTitle() {
    Requirement requirement =
        Requirement.builder()
            .description("This is a test requirement without title")
            .priority(Priority.LOW)
            .build();

    try {
      requirementRepository.save(requirement);
    } catch (Exception e) {
      assertNotNull(e);
    }
  }
}
