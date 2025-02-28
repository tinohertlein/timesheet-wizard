package dev.hertlein.timesheetwizard.core

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

@DisplayName("Architecture")
class ArchitectureTest {

    @Nested
    inner class Modules {

        private val basePackage = "dev.hertlein.timesheetwizard.core"
        private val modules = ApplicationModules.of(TestApplication::class.java)

        @Test
        fun `should have no dependencies on each other`() {
            modules.forEach { println(it) }
            modules.verify()
        }

        @Test
        @Disabled("Disabled by default, as there is no need to generate module documentation with each build.")
        fun `can be used for module documentation generation`() {
            // Please find the output in directory tw-core/build/spring-modulith-docs
            Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
        }

        @Nested
        inner class Import {

            @Test
            fun `should adhere to ports & adapters architecture`() {
                val classes = ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages("$basePackage._import")

                layeredArchitecture()
                    .consideringAllDependencies()

                    .layer("Domain Model").definedBy("..domain.model..")
                    .layer("Domain Services").definedBy("..domain.service..")
                    .layer("Domain Ports").definedBy("..domain.port..")
                    .layer("Clockify Adapter").definedBy("..adapter.outgoing.clockify..")
                    .layer("Eventing Adapter").definedBy("..adapter.outgoing.eventing..")

                    .whereLayer("Domain Services").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Domain Ports").mayOnlyBeAccessedByLayers("Domain Services", "Clockify Adapter", "Eventing Adapter")
                    .whereLayer("Clockify Adapter").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Eventing Adapter").mayNotBeAccessedByAnyLayer()

                    .check(classes)
            }
        }

        @Nested
        inner class Export {

            @Test
            fun `should adhere to ports & adapters architecture`() {
                val classes = ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages("$basePackage.export")

                layeredArchitecture()
                    .consideringAllDependencies()

                    .layer("Domain Model").definedBy("..domain.model..")
                    .layer("Domain Services").definedBy("..domain.service..")
                    .layer("Domain Ports").definedBy("..domain.port..")
                    .layer("Persistence Adapter").definedBy("..adapter.outgoing.persistence..")
                    .layer("Eventing Adapter").definedBy("..adapter.incoming.eventing..")

                    .whereLayer("Domain Services").mayOnlyBeAccessedByLayers("Eventing Adapter")
                    .whereLayer("Domain Ports").mayOnlyBeAccessedByLayers("Domain Services", "Persistence Adapter")
                    .whereLayer("Persistence Adapter").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Eventing Adapter").mayNotBeAccessedByAnyLayer()

                    .check(classes)
            }

        }

    }
}