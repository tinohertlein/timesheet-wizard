package dev.hertlein.timesheetwizard.core

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

@DisplayName("Architecture")
class ArchitectureTest {

    @Nested
    inner class Modules {

        private val modules = ApplicationModules.of(TestApplication::class.java)

        @Test
        fun `should have no dependencies on each other`() {
            modules.verify()
        }
    }
}