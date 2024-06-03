import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    val kotlin = "2.0.0"
    kotlin("jvm") version kotlin
    kotlin("plugin.allopen") version kotlin
    id("io.quarkus") version "3.11.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

group = "dev.hertlein.timesheetwizard"

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
    maven {
        name = "timesheet-wizard"
        url = uri("https://maven.pkg.github.com/tinohertlein/timesheet-wizard")
        credentials {
            username = ""
            password = System.getenv("GH_PACKAGE_TOKEN")
        }
    }
    maven {
        name = "timesheet-wizard-private"
        url = uri("https://maven.pkg.github.com/tinohertlein/timesheet-wizard-private")
        credentials {
            username = ""
            password = System.getenv("GH_PACKAGE_TOKEN")
        }
    }
}

val quarkusPlatformGroupId = "io.quarkus.platform"
val quarkusPlatformArtifactId = "quarkus-bom"
val quarkusPlatformVersion = "3.11.0"

dependencies {
    val guavaVersion = "33.2.1-jre"
    val kotlinLoggingVersion = "3.0.5"
    val twSpiVersion = "2.1.0"

    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))
    implementation("io.quarkiverse.amazonservices:quarkus-amazon-s3")

    implementation("io.quarkus:quarkus-amazon-lambda")
    implementation("io.quarkus:quarkus-rest-client-reactive-kotlin-serialization")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("dev.hertlein.timesheetwizard:documents-generator-spi:$twSpiVersion")


    val assertJVersion = "3.26.0"
    val mockkVersion = "1.13.11"

    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")


    val twCustomersPublicVersion = "2.1.0"
    val twCustomersPrivateVersion = "1.2.0"

    runtimeOnly("software.amazon.awssdk:url-connection-client")
    runtimeOnly("dev.hertlein.timesheetwizard:documents-generator-customers-public:$twCustomersPublicVersion")

    // remove this line to build the module, as this is package is not public
    runtimeOnly("dev.hertlein.timesheetwizard:documents-generator-customers-private:$twCustomersPrivateVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        javaParameters.set(true)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging.events = setOf(
        PASSED,
        SKIPPED,
        FAILED,
        STANDARD_OUT,
        STANDARD_ERROR
    )
}
