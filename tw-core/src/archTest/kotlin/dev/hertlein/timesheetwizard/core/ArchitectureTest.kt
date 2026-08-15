package dev.hertlein.timesheetwizard.core

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val BASE_PACKAGE = "dev.hertlein.timesheetwizard.core"
private const val IMPORTING_PACKAGE = "..importing.."
private const val EXPORTING_PACKAGE = "..exporting.."
private const val ANTICORRUPTION_PACKAGE = "..anticorruption.."

@DisplayName("Architecture")
class ArchitectureTest {

    private val classes = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
        .importPackages(BASE_PACKAGE)

    @Nested
    inner class Classes {

        @Test
        fun `must reside within their respective domain`() {
            ArchRuleDefinition.classes().that()
                .resideInAPackage("$BASE_PACKAGE..")
                .should()
                .resideInAnyPackage(IMPORTING_PACKAGE, EXPORTING_PACKAGE, ANTICORRUPTION_PACKAGE)
                .check(classes)
        }
    }

    @Nested
    inner class Module {

        @Test
        fun `import and export should have no dependencies on each other`() {
            layeredArchitecture()
                .consideringAllDependencies()

                .layer("Import").definedBy(IMPORTING_PACKAGE)
                .layer("Export").definedBy(EXPORTING_PACKAGE)
                .layer("Anticorruption").definedBy(ANTICORRUPTION_PACKAGE)

                .whereLayer("Import").mayOnlyBeAccessedByLayers("Anticorruption")
                .whereLayer("Export").mayOnlyBeAccessedByLayers("Anticorruption")

                .check(classes)
        }

        @Nested
        inner class Import {

            @Test
            fun `should adhere to ports & adapters architecture`() {
                val importing = "$BASE_PACKAGE.importing"
                val classes = ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
                    .importPackages(importing)

                layeredArchitecture()
                    .consideringAllDependencies()

                    .layer("Domain Model").definedBy("$importing.domain.model..")
                    .layer("Domain Service").definedBy("$importing..domain.service..")
                    .layer("Domain Port").definedBy("$importing..domain.port..")
                    .layer("Adapter").definedBy("$importing..adapter..")

                    .whereLayer("Domain Service").mayOnlyBeAccessedByLayers("Adapter")
                    .whereLayer("Domain Port").mayOnlyBeAccessedByLayers("Domain Service", "Adapter")
                    .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()

                    .check(classes)
            }
        }

        @Nested
        inner class Export {

            @Test
            fun `should adhere to ports & adapters architecture`() {
                val exporting = "$BASE_PACKAGE.exporting"
                val classes = ClassFileImporter()
                    .withImportOption(ImportOption.DoNotIncludeTests())
                    .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
                    .importPackages(exporting)

                layeredArchitecture()
                    .consideringAllDependencies()

                    .layer("Domain Model").definedBy("$exporting.domain.model..")
                    .layer("Domain Service").definedBy("$exporting.domain.service..")
                    .layer("Domain Port").definedBy("$exporting.domain.port..")
                    .layer("Adapter").definedBy("$exporting.adapter..")

                    .whereLayer("Domain Service").mayOnlyBeAccessedByLayers("Adapter")
                    .whereLayer("Domain Port").mayOnlyBeAccessedByLayers("Domain Service", "Adapter")
                    .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()

                    .check(classes)
            }
        }
    }
}