package org.blackbird.requirefortesting.requirements.api;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.requirements.model.CreateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.service.RequirementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
public class RequirementController {

  private final RequirementService requirementService;

  @PreAuthorize("hasAnyRole('REQUIREMENTS_ENGINEER')")
  @PostMapping
  public ResponseEntity<Requirement> createRequirement(
      @RequestBody CreateRequirementDto requirement) {
    Requirement newRequirement = requirementService.createRequirement(requirement);
    return ResponseEntity.ok(newRequirement);
  }
}
