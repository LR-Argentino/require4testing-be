package org.blackbird.requirefortesting.requirements.service;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.requirements.model.CreateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.repository.RequirementRepository;
import org.blackbird.requirefortesting.shared.Priority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequirementServiceImpl implements RequirementService {

  private final RequirementRepository requirementRepository;

  @Override
  public Requirement createRequirement(CreateRequirementDto createRequirement) {
    if (createRequirement == null) {
      throw new IllegalArgumentException("Requirement data cannot be null");
    }
    String specialCharactersRegex = ".*[^a-zA-Z0-9 ].*";

    if (createRequirement.title().isBlank()
        || createRequirement.title().matches(specialCharactersRegex)) {
      throw new IllegalArgumentException(
          "Requirement title cannot be empty or contain special characters");
    }
    Requirement newRequirement = requirementRepository.save(mapToRequirement(createRequirement));

    return newRequirement;
  }

  private Requirement mapToRequirement(CreateRequirementDto createRequirement) {
    Requirement requirement = new Requirement();
    requirement.setTitle(createRequirement.title());
    requirement.setDescription(createRequirement.description());
    requirement.setPriority(
        createRequirement.priority() != null ? createRequirement.priority() : Priority.LOW);
    return requirement;
  }
}
