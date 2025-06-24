
/**
 * Test Execution Context Module
 * Verantwortlichkeiten:
 * - Tests durchführen und Ergebnisse dokumentieren
 * - TestExecution Management
 * - Event Publishing: TestResultSubmitted
 * - Event Consuming: TestCaseAssigned
 */
@ApplicationModule(
        displayName = "Test Execution",
        allowedDependencies = {"shared"}
)
package org.blackbird.requirefortesting.testexecution;

import org.springframework.modulith.ApplicationModule;