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

    testImplementation(libs.bundles.testing)
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
    appName = "timesheet-wizard-app"
    resourceGroup = "timesheet-wizard"
    region = "Germany West Central"
    appServicePlanName = "VirtualDedicatedPlan"
    pricingTier = "Y1"

    runtime = GradleRuntimeConfig()
    runtime.os("linux")
    runtime.javaVersion("21")
    auth = GradleAuthConfig()
    auth.type = "azure_cli"

    appSettings = mutableMapOf()
    appSettings["TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY"] = System.getenv("TW_IMPORT_CLOCKIFY_API_KEY")
    appSettings["TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID"] = System.getenv("TW_IMPORT_CLOCKIFY_WORKSPACE_ID")
    appSettings["SPRING_CLOUD_AZURE_STORAGE_CONNECTION_STRING"] = System.getenv("SPRING_CLOUD_AZURE_STORAGE_CONNECTION_STRING")
    appSettings["applicationInsights.samplingSettings.isEnabled"] = "false"
    appSettings["FUNCTIONS_EXTENSION_VERSION"] = "~4"

    // Uncomment to enable local debug
    // localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"
}