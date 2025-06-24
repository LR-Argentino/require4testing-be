package org.blackbird.requirefortesting.testmanagement.model;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a Test Run. Notice that the <code>Status</code> is not included
 * here, as it will be set to <b>PLANNED</b> by default in the service layer and there is a seperate
 * method for updating the status of a Test Run.
 *
 * @param title
 * @param startDate
 * @param endDate
 */
public record CreateTestRunDto(
    String title, String description, LocalDateTime startDate, LocalDateTime endDate) {}
