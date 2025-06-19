package org.blackbird.requirefortesting.requirements.api;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
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

  @PreAuthorize("hasRole('REQUIREMENTS_ENGINEER')")
  @PostMapping
  public ResponseEntity<Requirement> createRequirement(
      @RequestBody CreateOrUpdateRequirementDto requirement) {
    Requirement newRequirement = requirementService.createRequirement(requirement);
    return ResponseEntity.ok(newRequirement);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Requirement> updateRequirement(
      @PathVariable Long id, @RequestBody CreateOrUpdateRequirementDto requirement) {
    Requirement updatedRequirement = requirementService.updateRequirement(id, requirement);
    return ResponseEntity.ok(updatedRequirement);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
    requirementService.deleteRequirement(id);
    return ResponseEntity.noContent().build();
  }
}
