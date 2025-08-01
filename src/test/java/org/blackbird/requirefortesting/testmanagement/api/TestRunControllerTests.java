package org.blackbird.requirefortesting.testmanagement.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.blackbird.requirefortesting.security.internal.JwtUtil;
import org.blackbird.requirefortesting.security.model.User;
import org.blackbird.requirefortesting.shared.Status;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestCaseRepository;
import org.blackbird.requirefortesting.testmanagement.internal.repository.TestRunRepository;
import org.blackbird.requirefortesting.testmanagement.model.CreateTestRunDto;
import org.blackbird.requirefortesting.testmanagement.model.TestCase;
import org.blackbird.requirefortesting.testmanagement.model.TestRun;
import org.blackbird.requirefortesting.testmanagement.model.TestRunStatus;
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
class TestRunControllerTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtil jwtUtil;

  @Autowired private TestRunRepository testRunRepository;
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
  void test_createTestRun_shouldPersistToDatabase() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    CreateTestRunDto dto =
        new CreateTestRunDto(
            "Integration Test Run", "Real test run", startTime, endTime, List.of(1L, 2L));

    MvcResult result =
        mockMvc
            .perform(
                post("/api/test-runs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                    .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration Test Run"))
            .andExpect(jsonPath("$.status").value("PLANNED"))
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

    List<TestRun> saved = testRunRepository.findAll();
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getTitle()).isEqualTo("Integration Test Run");
    assertThat(saved.get(0).getStatus()).isEqualTo(TestRunStatus.PLANNED);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestRun_shouldModifyExisting() throws Exception {
    LocalDateTime originalStartTime = LocalDateTime.now().plusDays(1);
    LocalDateTime originalEndTime = LocalDateTime.now().plusDays(2);

    TestRun existing =
        testRunRepository.save(
            TestRun.builder()
                .title("Original Test Run")
                .description("Original description")
                .startTime(originalStartTime)
                .endTime(originalEndTime)
                .status(TestRunStatus.PLANNED)
                .createdBy(1L)
                .build());

    LocalDateTime newStartTime = LocalDateTime.now().plusDays(3);
    LocalDateTime newEndTime = LocalDateTime.now().plusDays(4);

    CreateTestRunDto updateDto =
        new CreateTestRunDto(
            "Updated Test Run", "Updated Description", newStartTime, newEndTime, List.of(1L, 2L));

    mockMvc
        .perform(
            put("/api/test-runs/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Test Run"))
        .andExpect(jsonPath("$.description").value("Updated Description"));

    Optional<TestRun> updated = testRunRepository.findById(existing.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getTitle()).isEqualTo("Updated Test Run");
    assertThat(updated.get().getDescription()).isEqualTo("Updated Description");
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_deleteTestRun_shouldRemoveFromDatabase() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    TestRun existing =
        testRunRepository.save(
            TestRun.builder()
                .title("To be deleted")
                .description("This will be deleted")
                .startTime(startTime)
                .endTime(endTime)
                .status(TestRunStatus.PLANNED)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(delete("/api/test-runs/{id}", existing.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    Optional<TestRun> deleted = testRunRepository.findById(existing.getId());
    assertThat(deleted).isNotPresent();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getTestRunById_shouldReturnExistingTestRun() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    TestRun existing =
        testRunRepository.save(
            TestRun.builder()
                .title("Existing Test Run")
                .description("Existing description")
                .startTime(startTime)
                .endTime(endTime)
                .status(TestRunStatus.PLANNED)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(get("/api/test-runs/{id}", existing.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Existing Test Run"))
        .andExpect(jsonPath("$.status").value("PLANNED"))
        .andExpect(jsonPath("$.id").value(existing.getId()));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getTestRunById_shouldReturn404WhenNotFound() throws Exception {
    mockMvc
        .perform(get("/api/test-runs/{id}", 99999L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_getAllTestRuns_shouldReturnAllTestRuns() throws Exception {
    LocalDateTime startTime1 = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime1 = LocalDateTime.now().plusDays(2);
    LocalDateTime startTime2 = LocalDateTime.now().plusDays(3);
    LocalDateTime endTime2 = LocalDateTime.now().plusDays(4);

    testRunRepository.save(
        TestRun.builder()
            .title("First Test Run")
            .startTime(startTime1)
            .endTime(endTime1)
            .status(TestRunStatus.PLANNED)
            .createdBy(1L)
            .build());

    testRunRepository.save(
        TestRun.builder()
            .title("Second Test Run")
            .startTime(startTime2)
            .endTime(endTime2)
            .status(TestRunStatus.IN_PROGRESS)
            .createdBy(1L)
            .build());

    mockMvc
        .perform(get("/api/test-runs"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));

    List<TestRun> all = testRunRepository.findAll();
    assertThat(all).hasSize(2);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_addTestCaseToTestRun_shouldCreateRelationship() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    TestRun testRun =
        testRunRepository.save(
            TestRun.builder()
                .title("Test Run with Cases")
                .startTime(startTime)
                .endTime(endTime)
                .status(TestRunStatus.PLANNED)
                .createdBy(1L)
                .build());

    TestCase testCase =
        testCaseRepository.save(
            TestCase.builder()
                .title("Test Case 1")
                .description("Test case description")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(
            post(
                "/api/test-runs/{testRunId}/test-cases/{testCaseId}",
                testRun.getId(),
                testCase.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    // Verify the relationship was created
    mockMvc
        .perform(get("/api/test-runs/{id}", testRun.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.testCases").isArray())
        .andExpect(jsonPath("$.testCases.length()").value(1))
        .andExpect(jsonPath("$.testCases[0].id").value(testCase.getId()));

    Optional<TestRun> updated = testRunRepository.findById(testRun.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getTestCases()).hasSize(1);
    assertThat(updated.get().getTestCases().iterator().next().getId()).isEqualTo(testCase.getId());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_addTestCaseToTestRun_shouldReturn404WhenTestRunNotFound() throws Exception {
    TestCase testCase =
        testCaseRepository.save(
            TestCase.builder()
                .title("Test Case 1")
                .description("Test case description")
                .requirementId(1L)
                .status(Status.OPEN)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(
            post("/api/test-runs/{testRunId}/test-cases/{testCaseId}", 99999L, testCase.getId()))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_addTestCaseToTestRun_shouldReturn404WhenTestCaseNotFound() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    TestRun testRun =
        testRunRepository.save(
            TestRun.builder()
                .title("Test Run")
                .startTime(startTime)
                .endTime(endTime)
                .status(TestRunStatus.PLANNED)
                .createdBy(1L)
                .build());

    mockMvc
        .perform(
            post("/api/test-runs/{testRunId}/test-cases/{testCaseId}", testRun.getId(), 99999L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestRun_shouldReturn400WhenTitleIsNull() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    CreateTestRunDto invalidDto =
        new CreateTestRunDto(null, "Valid description", startTime, endTime, List.of(1L, 2L));

    mockMvc
        .perform(
            post("/api/test-runs")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestRun> saved = testRunRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestRun_shouldReturn400WhenStartTimeIsNull() throws Exception {
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    CreateTestRunDto invalidDto =
        new CreateTestRunDto("Valid Title", "Valid description", null, endTime, List.of(1L, 2L));

    mockMvc
        .perform(
            post("/api/test-runs")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestRun> saved = testRunRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestRun_shouldReturn400WhenEndTimeIsNull() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);

    CreateTestRunDto invalidDto =
        new CreateTestRunDto("Valid Title", "Valid description", startTime, null, List.of(1L, 2L));

    mockMvc
        .perform(
            post("/api/test-runs")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestRun> saved = testRunRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_createTestRun_shouldReturn400WhenEndTimeIsBeforeStartTime() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(2);
    LocalDateTime endTime = LocalDateTime.now().plusDays(1); // Before start time

    CreateTestRunDto invalidDto =
        new CreateTestRunDto(
            "Valid Title", "Valid description", startTime, endTime, List.of(1L, 2L));

    mockMvc
        .perform(
            post("/api/test-runs")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateValidJwtToken())
                .content(objectMapper.writeValueAsString(invalidDto)))
        .andDo(print())
        .andExpect(status().isBadRequest());

    List<TestRun> saved = testRunRepository.findAll();
    assertThat(saved).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_updateTestRun_shouldReturn404WhenTestRunNotFound() throws Exception {
    LocalDateTime startTime = LocalDateTime.now().plusDays(1);
    LocalDateTime endTime = LocalDateTime.now().plusDays(2);

    CreateTestRunDto updateDto =
        new CreateTestRunDto(
            "Updated Title", "Updated description", startTime, endTime, List.of(1L, 2L));

    mockMvc
        .perform(
            put("/api/test-runs/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void test_deleteTestRun_shouldReturn404WhenTestRunNotFound() throws Exception {
    mockMvc
        .perform(delete("/api/test-runs/{id}", 99999L))
        .andDo(print())
        .andExpect(status().isNotFound());
  }
}
