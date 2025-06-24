package org.blackbird.requirefortesting.testmanagement.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.blackbird.requirefortesting.security.internal.JwtUtil;
import org.blackbird.requirefortesting.security.model.User;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateOrUpdateTestCaseDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class TestCaseControllerTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @Autowired private TestCaseRepository testCaseRepository;

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

  private String generateValidJwtToken() {
    User testUser =
        User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_REQUIREMENTS_ENGINEER")))
            .enabled(true)
            .build();

    return jwtUtil.generateToken(testUser);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldPersistToDatabase() throws Exception {
    CreateOrUpdateTestCaseDto dto =
        new CreateOrUpdateTestCaseDto("Integration Test Case", "Real test case", 1L, Status.OPEN);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/test-cases")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                    .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration Test Case"))
            .andExpect(jsonPath("$.status").value("OPEN"))
            .andExpect(jsonPath("$.requirementId").value(1))
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getTitle()).isEqualTo("Integration Test Case");
    assertThat(saved.get(0).getStatus()).isEqualTo(Status.OPEN);
    assertThat(saved.get(0).getRequirementId()).isEqualTo(1L);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestCase_shouldModifyExisting() throws Exception {
    TestCase existing =
        testCaseRepository.save(
            TestCase.builder()
                .title("Original Test Case")
                .description("Original description")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    CreateOrUpdateTestCaseDto updateDto =
        new CreateOrUpdateTestCaseDto(
            "Updated Test Case", "Updated Description", 2L, Status.CLOSED);

    mockMvc
        .perform(
            put("/api/test-cases/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Test Case"))
        .andExpect(jsonPath("$.description").value("Updated Description"))
        .andExpect(jsonPath("$.requirementId").value(2))
        .andExpect(jsonPath("$.status").value("CLOSED"));

    Optional<TestCase> updated = testCaseRepository.findById(existing.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getTitle()).isEqualTo("Updated Test Case");
    assertThat(updated.get().getDescription()).isEqualTo("Updated Description");
    assertThat(updated.get().getRequirementId()).isEqualTo(2L);
    assertThat(updated.get().getStatus()).isEqualTo(Status.CLOSED);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_deleteTestCase_shouldRemoveFromDatabase() throws Exception {
    TestCase existing =
        testCaseRepository.save(
            TestCase.builder()
                .title("To be deleted")
                .description("This will be deleted")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(post("/api/test-cases/{id}", existing.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    Optional<TestCase> deleted = testCaseRepository.findById(existing.getId());
    assertThat(deleted).isNotPresent();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getTestCaseById_shouldReturnExistingTestCase() throws Exception {
    TestCase existing =
        testCaseRepository.save(
            TestCase.builder()
                .title("Existing Test Case")
                .description("Existing description")
                .requirementId(1L)
                .status(Status.IN_PROGRESS)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(get("/api/test-cases/{id}", existing.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Existing Test Case"))
        .andExpect(jsonPath("$.description").value("Existing description"))
        .andExpect(jsonPath("$.requirementId").value(1))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.id").value(existing.getId()));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getTestCaseById_shouldReturn404WhenNotFound() throws Exception {
    mockMvc
        .perform(get("/api/test-cases/{id}", 99999L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getAllTestCases_shouldReturnAllTestCases() throws Exception {
    testCaseRepository.save(
        TestCase.builder()
            .title("First Test Case")
            .description("First description")
            .requirementId(1L)
            .status(Status.OPEN)
            .createdBy(1L)
            .build());

    testCaseRepository.save(
        TestCase.builder()
            .title("Second Test Case")
            .description("Second description")
            .requirementId(2L)
            .status(Status.CLOSED)
            .createdBy(1L)
            .build());

    mockMvc
        .perform(get("/api/test-cases"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));

    List<TestCase> all = testCaseRepository.findAll();
    assertThat(all).hasSize(2);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldReturn400WhenTitleIsNull() throws Exception {
    CreateOrUpdateTestCaseDto invalidDto =
        new CreateOrUpdateTestCaseDto(null, "Valid description", 1L, Status.OPEN);

    mockMvc
        .perform(
            post("/api/test-cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldCreateWithMinimalData() throws Exception {
    CreateOrUpdateTestCaseDto minimalDto =
        new CreateOrUpdateTestCaseDto("Minimal Test Case", null, 1L, Status.OPEN);

    mockMvc
        .perform(
            post("/api/test-cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(minimalDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Minimal Test Case"))
        .andExpect(jsonPath("$.description").isEmpty())
        .andExpect(jsonPath("$.requirementId").value(1))
        .andExpect(jsonPath("$.status").value("OPEN"));

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getTitle()).isEqualTo("Minimal Test Case");
    assertThat(saved.get(0).getDescription()).isNull();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestCase_shouldReturn404WhenTestCaseNotFound() throws Exception {
    CreateOrUpdateTestCaseDto updateDto =
        new CreateOrUpdateTestCaseDto("Updated Title", "Updated description", 1L, Status.CLOSED);

    mockMvc
        .perform(
            put("/api/test-cases/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestCase_shouldReturn400WhenTitleIsNull() throws Exception {
    TestCase existing =
        testCaseRepository.save(
            TestCase.builder()
                .title("Existing Test Case")
                .description("Existing description")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    CreateOrUpdateTestCaseDto invalidDto =
        new CreateOrUpdateTestCaseDto(null, "Updated description", 1L, Status.CLOSED);

    mockMvc
        .perform(
            put("/api/test-cases/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // Verify no changes were made
    Optional<TestCase> unchanged = testCaseRepository.findById(existing.getId());
    assertThat(unchanged).isPresent();
    assertThat(unchanged.get().getTitle()).isEqualTo("Existing Test Case");
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_deleteTestCase_shouldReturn404WhenTestCaseNotFound() throws Exception {
    mockMvc
        .perform(post("/api/test-cases/{id}", 99999L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldHandleVeryLongTitle() throws Exception {
    String longTitle = "A".repeat(1000);
    CreateOrUpdateTestCaseDto longTitleDto =
        new CreateOrUpdateTestCaseDto(longTitle, "Valid description", 1L, Status.OPEN);

    mockMvc
        .perform(
            post("/api/test-cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(longTitleDto)))
        .andDo(print())
        .andExpect(status().isBadRequest()); // Should fail validation

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldHandleMalformedJson() throws Exception {
    mockMvc
        .perform(
            post("/api/test-cases")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content("{\"invalid\":\"json\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestCase_shouldHandleMissingContentTypeHeader() throws Exception {
    CreateOrUpdateTestCaseDto dto =
        new CreateOrUpdateTestCaseDto("Valid Test Case", "Valid description", 1L, Status.OPEN);

    mockMvc
        .perform(post("/api/test-cases").content(objectMapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().is5xxServerError());

    List<TestCase> saved = testCaseRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestCase_shouldUpdatePartialFields() throws Exception {
    TestCase existing =
        testCaseRepository.save(
            TestCase.builder()
                .title("Original Title")
                .description("Original description")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    CreateOrUpdateTestCaseDto partialUpdateDto =
        new CreateOrUpdateTestCaseDto(
            "Updated Title Only",
            existing.getDescription(),
            existing.getRequirementId(),
            Status.IN_PROGRESS);

    mockMvc
        .perform(
            put("/api/test-cases/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdateDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title Only"))
        .andExpect(jsonPath("$.description").value("Original description"))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

    Optional<TestCase> updated = testCaseRepository.findById(existing.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getTitle()).isEqualTo("Updated Title Only");
    assertThat(updated.get().getDescription()).isEqualTo("Original description");
    assertThat(updated.get().getStatus()).isEqualTo(Status.IN_PROGRESS);
  }
}
