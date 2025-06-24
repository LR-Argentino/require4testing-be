package org.blackbird.requirefortesting;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

  @Test
  void testModularity() {
    ApplicationModules modules = ApplicationModules.of("org.blackbird.requirefortesting");

    new Documenter(modules)
        .writeModulesAsPlantUml()
        .writeDocumentation()
        .writeIndividualModulesAsPlantUml();
    System.out.println(modules);
    modules.verify();
  }

  @Test
  void noCyclesBetweenModules() {
    SlicesRuleDefinition.slices()
        .matching("org.blackbird.requirefortesting.(*)..")
        .should()
        .beFreeOfCycles()
        .check(new ClassFileImporter().importPackages("org.blackbird.requirefortesting"));
  }
}
