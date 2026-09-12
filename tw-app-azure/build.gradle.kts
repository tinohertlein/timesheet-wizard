import com.microsoft.azure.gradle.auth.GradleAuthConfig
import com.microsoft.azure.gradle.configuration.GradleRuntimeConfig

plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.azure)
    alias(libs.plugins.javaagent.test)
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.app)
    alias(libs.plugins.micronaut.aot)
}

version = "dummy"

apply {
    libs.plugins.azure
}

application {
    mainClass = "dev.hertlein.timesheetwizard.app.azure.TwAzureApplication"
}

micronaut {
    runtime("azure_function")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("dev.hertlein.timesheetwizard.*")
    }
    aot {
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

azurefunctions {
    resourceGroup = System.getenv("AZURE_RESOURCE_GROUP") ?: "timesheetwizard"
    appName = System.getenv("AZURE_FUNCTIONAPP_NAME") ?: "tw-app-azure"
    region = System.getenv("AZURE_LOCATION") ?: "germanywestcentral"

    runtime = GradleRuntimeConfig()
    runtime.javaVersion("25")
    auth = GradleAuthConfig()
    auth.type = "azure_cli"

    appSettings = mutableMapOf()
    appSettings["CLOCKIFY_API_KEY"] = System.getenv("CLOCKIFY_API_KEY")
    appSettings["CLOCKIFY_WORKSPACE_ID"] = System.getenv("CLOCKIFY_WORKSPACE_ID")

    // Uncomment to enable local debug
    // localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    compileOnly(libs.micronaut.http.client)

    ksp(libs.micronaut.http.validation)
    ksp(libs.micronaut.serde.processor)
            
    implementation(libs.bundles.azure)
    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))

    runtimeOnly(libs.jackson.kotlin)
    runtimeOnly(libs.logback)
    runtimeOnly(libs.snakeyaml)

    testJavaagent(libs.byte.buddy.agent)
    testAnnotationProcessor(libs.micronaut.inject.java)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.azure)
    testImplementation(testFixtures(project(":tw-core")))
}

repositories {
    mavenCentral()
}