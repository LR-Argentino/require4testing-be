/***
 * Security Module
 * Verantwortlichkeiten:
 * - Authentifizierung und Autorisierung
 * - Sicherheitskonfiguration
 * - User im Security Context speichern & abrufen
 */

@ApplicationModule(
    displayName = "Security Module",
    allowedDependencies = {"shared"})
package org.blackbird.requirefortesting.security;

import org.springframework.modulith.ApplicationModule;
