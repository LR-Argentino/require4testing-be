package org.blackbird.requirefortesting.requirements.model;

import org.blackbird.requirefortesting.shared.Priority;

public record CreateOrUpdateRequirementDto(String title, String description, Priority priority) {}
