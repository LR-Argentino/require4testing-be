package org.blackbird.requirefortesting.requirements.service;

import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.model.dto.CreateRequirementDto;

public interface RequirementService {
  Requirement createRequirement(CreateRequirementDto createRequirement) throws Exception;
}
