package org.blackbird.requirefortesting.requirements.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.service.RequirementService;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RequirementControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private RequirementService requirementService;
  @Autowired private RequirementRepository requirementRepository;

  @Test
  @WithMockUser(username = "engineer", roles = "REQUIREMENTS_ENGINEER")
  void test_createRequirement_shouldPersistToDatabase() throws Exception {
    CreateOrUpdateRequirementDto dto =
        new CreateOrUpdateRequirementDto("Integration Test", "Real test", Priority.HIGH);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/requirements")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration Test"))
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

    List<Requirement> saved = requirementRepository.findAll();
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getTitle()).isEqualTo("Integration Test");
  }

  @Test
  @WithMockUser(username = "engineer", roles = "REQUIREMENTS_ENGINEER")
  void test_updateRequirement_shouldModifyExisting() throws Exception {
    Requirement existing =
        requirementRepository.save(
            Requirement.builder()
                .title("Original")
                .description("Original desc")
                .priority(Priority.LOW)
                .status(Status.OPEN)
                .build());

    CreateOrUpdateRequirementDto updateDto =
        new CreateOrUpdateRequirementDto("Updated Title", "Updated Description", Priority.HIGH);

    mockMvc
        .perform(
            put("/api/requirements/{id}", existing.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.priority").value("HIGH"));

    Optional<Requirement> updated = requirementRepository.findById(existing.getId());
    assertThat(updated).isPresent();
    assertThat(updated.get().getTitle()).isEqualTo("Updated Title");
  }
}
