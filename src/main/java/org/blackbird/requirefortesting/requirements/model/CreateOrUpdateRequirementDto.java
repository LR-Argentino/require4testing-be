package org.blackbird.requirefortesting.requirements.model;

import org.blackbird.requirefortesting.shared.Priority;
import org.blackbird.requirefortesting.shared.Status;

public record CreateOrUpdateRequirementDto(
    String title, String description, Priority priority, Status status) {}
