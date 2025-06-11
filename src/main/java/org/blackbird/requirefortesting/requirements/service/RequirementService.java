package org.blackbird.requirefortesting.requirements.service;

import org.blackbird.requirefortesting.requirements.model.CreateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;

public interface RequirementService {
  Requirement createRequirement(CreateRequirementDto createRequirement);
}
