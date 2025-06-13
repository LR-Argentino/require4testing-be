package org.blackbird.requirefortesting.testmanagement.model;

import org.blackbird.requirefortesting.shared.Status;

public record CreateTestCaseDto(
    String title, String description, Long requirementId, Status status) {}
