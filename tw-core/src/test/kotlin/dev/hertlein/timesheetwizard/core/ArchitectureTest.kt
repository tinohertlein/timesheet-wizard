package dev.hertlein.timesheetwizard.core

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Architecture")
class ArchitectureTest {

    @Nested
    inner class Modules {

        private val basePackage = "dev.hertlein.timesheetwizard.core"

        @Test
        fun `import and export should have no dependencies on each other`() {
            val classes = ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages(basePackage)

            layeredArchitecture()
                .consideringAllDependencies()

                .layer("Import").definedBy("..importing..")
                .layer("Export").definedBy("..exporting..")
                .layer("Anticorruption").definedBy("..anticorruption..")

                .whereLayer("Import").mayOnlyBeAccessedByLayers("Anticorruption")
                .whereLayer("Export").mayOnlyBeAccessedByLayers("Anticorruption")

                .check(classes)
        }

        @Nested
        inner class Import {

            @Test
            fun `should adhere to ports & adapters architecture`() {
                val classes = ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .importPackages("$basePackage.importing")

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
                    .importPackages("$basePackage.exporting")

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