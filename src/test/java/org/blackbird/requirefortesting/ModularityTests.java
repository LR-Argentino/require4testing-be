package org.blackbird.requirefortesting;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    @Test
    void testModularity() {
        ApplicationModules modules = ApplicationModules.of("org.blackbird.requirefortesting");

        System.out.println(modules);
        modules.verify();
    }
}
