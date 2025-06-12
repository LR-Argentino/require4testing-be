package org.blackbird.requirefortesting.requirements.service;

import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.requirements.model.CreateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.repository.RequirementRepository;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
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

    validateRequirementTitle(createRequirement.title());

    Requirement newRequirement = requirementRepository.save(mapToRequirement(createRequirement));

    return newRequirement;
  }

  @Override
  public Requirement updateRequirement(Long id, CreateRequirementDto updateRequirement) {

    if (updateRequirement == null) {
      throw new IllegalArgumentException("Update data cannot be null");
    }

    Requirement requirement =
        requirementRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new IllegalArgumentException("Requirement with id " + id + " does not exist"));

    if (requirement.getStatus() != Status.OPEN) {
      throw new IllegalStateException("Requirement cannot be updated because it is not open");
    }

    validateRequirementTitle(updateRequirement.title());

    requirement.setTitle(updateRequirement.title());
    requirement.setDescription(updateRequirement.description());

    if (updateRequirement.priority() != null) {
      requirement.setPriority(updateRequirement.priority());
    }

    return requirement;
  }

  private Requirement mapToRequirement(CreateRequirementDto createRequirement) {
    Requirement requirement = new Requirement();
    requirement.setTitle(createRequirement.title());
    requirement.setDescription(createRequirement.description());
    requirement.setPriority(
        createRequirement.priority() != null ? createRequirement.priority() : Priority.LOW);
    return requirement;
  }

  private void validateRequirementTitle(String title) {
    String specialCharRegex = ".*[^a-zA-Z0-9 ].*";
    if (title.isBlank() || title.matches(specialCharRegex)) {
      throw new IllegalArgumentException(
          "Requirement title cannot be empty or contain special characters");
    }
  }
}
