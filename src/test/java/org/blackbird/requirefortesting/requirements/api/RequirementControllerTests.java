package org.blackbird.requirefortesting.requirements.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RequirementControllerTests {
  @Autowired private MockMvc mockMvc;

  @Test
  void test_createWithInvalidUserRole_shouldReturnForbidden() throws Exception {
    mockMvc
        .perform(
            post("/api/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\": \"Test Requirement\", \"description\": \"Test Description\", \"priority\": \"HIGH\"}"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "engineer", roles = "REQUIREMENTS_ENGINEER")
  void test_createWithValidUserRole_shouldReturnOk() throws Exception {
    mockMvc
        .perform(
            post("/api/requirements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"title\": \"Test Requirement\", \"description\": \"Test Description\", \"priority\": \"HIGH\"}"))
        .andExpect(status().isOk());
  }
}
