package org.blackbird.requirefortesting.requirements.model.dto;

import org.blackbird.requirefortesting.shared.Priority;

public record CreateRequirementDto(String title, String description, Priority priority) {}
