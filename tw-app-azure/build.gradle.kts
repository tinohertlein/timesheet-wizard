import com.microsoft.azure.gradle.auth.GradleAuthConfig
import com.microsoft.azure.gradle.configuration.GradleRuntimeConfig

plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.thin)
    alias(libs.plugins.azure)
    alias(libs.plugins.javaagent.test)
}

version = "dummy"

apply {
    libs.plugins.azure
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi-cloud"))
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.web)
    implementation(platform(libs.spring.cloud))
    implementation(libs.bundles.spring.cloud)
    implementation(platform(libs.spring.cloud.azure))
    implementation(libs.bundles.spring.cloud.azure)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.azure)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.hertlein.timesheetwizard.app.azure.TwAzureApplication"
    }
}

azurefunctions {
    resourceGroup = System.getenv("AZURE_RESOURCE_GROUP") ?: "timesheetwizard"
    appName = System.getenv("AZURE_FUNCTIONAPP_NAME") ?: "timesheetwizardapp"
    region = System.getenv("AZURE_LOCATION") ?: "germanywestcentral"

    runtime = GradleRuntimeConfig()
    runtime.javaVersion("21")
    auth = GradleAuthConfig()
    auth.type = "azure_cli"

    appSettings = mutableMapOf()
    appSettings["TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY"] = System.getenv("TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY")
    appSettings["TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID"] = System.getenv("TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID")

    // Uncomment to enable local debug
    // localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"
}