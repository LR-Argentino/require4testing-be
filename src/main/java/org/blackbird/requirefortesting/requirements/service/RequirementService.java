package org.blackbird.requirefortesting.requirements.service;

import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;

public interface RequirementService {
  Requirement createRequirement(CreateOrUpdateRequirementDto createRequirement);

  Requirement updateRequirement(Long id, CreateOrUpdateRequirementDto updateRequirement);
}
