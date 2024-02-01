import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT

plugins {
    val kotlin = "1.9.22"
    kotlin("jvm") version kotlin
    kotlin("plugin.allopen") version kotlin
    id("io.quarkus") version "3.7.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
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
val quarkusPlatformVersion = "3.7.1"

dependencies {
    val guavaVersion = "33.0.0-jre"
    val kotlinLoggingVersion = "3.0.5"
    val twSpiVersion = "1.0.1"

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


    val assertJVersion = "3.25.2"
    val mockkVersion = "1.13.9"

    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")


    val twCustomersPublicVersion = "1.0.2"
    val twCustomersPrivateVersion = "0.8.3"

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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
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
