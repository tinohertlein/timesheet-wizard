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

    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("com.microsoft.azure.functions:azure-functions-java-library")
    implementation("io.micronaut.azure:micronaut-azure-sdk")
    implementation("io.micronaut.azure:micronaut-azure-function")
    implementation("io.micronaut.objectstorage:micronaut-object-storage-azure")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.4.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.10")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("tools.jackson.module:jackson-module-kotlin")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml")
    testImplementation("io.micronaut:micronaut-http-client")
    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.azure)
    testImplementation(testFixtures(project(":tw-core")))
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation("io.micronaut.test:micronaut-test-junit5:1.1.5")

}
repositories {
    mavenCentral()
}