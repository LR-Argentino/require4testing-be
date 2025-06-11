package org.blackbird.requirefortesting.requirements.service;

import org.blackbird.requirefortesting.requirements.model.dto.CreateRequirementDto;

public interface RequirementService {
  void createRequirement(CreateRequirementDto createRequirement);
}
