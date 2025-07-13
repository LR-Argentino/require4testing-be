package org.blackbird.requirefortesting.testmanagement.model;

import java.time.LocalDateTime;
import lombok.Builder;
import org.blackbird.requirefortesting.shared.Status;

@Builder
public record TestCaseDto(
    Long id,
    String title,
    String description,
    Long requirementId,
    Status status,
    TestResult testResult,
    Long createdBy,
    LocalDateTime updatedAt,
    LocalDateTime creationDate) {}