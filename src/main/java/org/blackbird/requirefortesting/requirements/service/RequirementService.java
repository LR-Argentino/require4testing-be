package org.blackbird.requirefortesting.requirements.service;

import java.util.List;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;

public interface RequirementService {
  Requirement createRequirement(CreateOrUpdateRequirementDto createRequirement, Long userId);

  Requirement updateRequirement(Long id, CreateOrUpdateRequirementDto updateRequirement);

  Requirement getRequirement(Long id);

  List<Requirement> getRequirements();

  void deleteRequirement(Long id);
}
