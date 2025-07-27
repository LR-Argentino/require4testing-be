package org.blackbird.requirefortesting.testexecution.model;

import org.blackbird.requirefortesting.testmanagement.model.TestResult;

public record UpdateTestResultDto(TestResult testResult, String comment) {}
