package org.blackbird.requirefortesting.requirements.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.blackbird.requirefortesting.requirements.internal.repository.RequirementRepository;
import org.blackbird.requirefortesting.requirements.model.CreateOrUpdateRequirementDto;
import org.blackbird.requirefortesting.requirements.model.Requirement;
import org.blackbird.requirefortesting.requirements.service.RequirementService;
import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequirementServiceImpl implements RequirementService {

  private final RequirementRepository requirementRepository;

  @Override
  @Transactional
  public Requirement createRequirement(CreateOrUpdateRequirementDto createRequirement) {
    if (createRequirement == null) {
      throw new IllegalArgumentException("Requirement data cannot be null");
    }

    validateRequirementTitle(createRequirement.title());

    Requirement newRequirement = requirementRepository.save(mapToRequirement(createRequirement));

    return newRequirement;
  }

  @Override
  @Transactional
  public Requirement updateRequirement(Long id, CreateOrUpdateRequirementDto updateRequirement) {

    if (updateRequirement == null) {
      throw new IllegalArgumentException("Update data cannot be null");
    }

    Requirement requirement = getRequirement(id);

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

  @Override
  @Transactional
  public void deleteRequirement(Long id) {
    Requirement requirement = getRequirement(id);

    requirementRepository.delete(requirement);
  }

  private Requirement mapToRequirement(CreateOrUpdateRequirementDto createRequirement) {
    Requirement requirement =
        Requirement.builder()
            .title(createRequirement.title())
            .description(createRequirement.description())
            .priority(
                createRequirement.priority() != null ? createRequirement.priority() : Priority.LOW)
            .build();

    return requirement;
  }

  @Override
  @Transactional(readOnly = true)
  public Requirement getRequirement(Long id) {
    return requirementRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  private void validateRequirementTitle(String title) {
    String specialCharRegex = ".*[^a-zA-Z0-9 ].*";
    if (title == null || title.isBlank() || title.matches(specialCharRegex)) {
      throw new IllegalArgumentException(
          "Requirement title cannot be empty or contain special characters");
    }
  }
}
