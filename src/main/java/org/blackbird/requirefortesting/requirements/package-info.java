/**
 * Requirements Context Module
 * Verantwortlichkeiten:
 * - Anforderungen erstellen, bearbeiten, löschen
 * - Status-Management (OPEN → IN_PROGRESS → CLOSED)
 * - Event Publishing: RequirementCreated
 * - Event Consuming: TestCaseCompleted
 */

@ApplicationModule(
        displayName = "Requirements Management",
        allowedDependencies = {"shared"}
)
package org.blackbird.requirefortesting.requirements;

import org.springframework.modulith.ApplicationModule;