/**
 * Test Management Context Module
 * Verantwortlichkeiten:
 * - Testfälle und Testläufe verwalten
 * - Testfall-Zuordnung zu Testläufen
 * - Event Publishing: TestCaseAssigned, TestRunStarted, TestRunCompleted
 * - Event Consuming: RequirementCreated
 */

@ApplicationModule(
        displayName = "Test Management",
        allowedDependencies = {"shared"}
)
package org.blackbird.requirefortesting.testmanagement;

import org.springframework.modulith.ApplicationModule;