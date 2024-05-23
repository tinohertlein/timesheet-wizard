import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.kordamp.gradle.jandex") version "2.0.0"
}

group = "dev.hertlein.timesheetwizard"
version = "2.1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tinohertlein/timesheet-wizard")
        credentials {
            username = ""
            password = System.getenv("GH_PACKAGE_TOKEN")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tinohertlein/timesheet-wizard")
            credentials {
                username = ""
                password = System.getenv("GH_PACKAGE_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

dependencies {
    val twSpiVersion = "2.1.0"
    val jakartaApiVersion = "2.0.1"
    val openCsvVersion = "5.9"
    val poiVersion = "5.2.5"
    val jasperVersion = "6.21.3"
    val openPdfVersion = "2.0.2"

    implementation("dev.hertlein.timesheetwizard:documents-generator-spi:$twSpiVersion")
    implementation("jakarta.inject:jakarta.inject-api:$jakartaApiVersion")
    implementation("com.opencsv:opencsv:$openCsvVersion")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("net.sf.jasperreports:jasperreports:$jasperVersion")
    implementation("com.github.librepdf:openpdf:$openPdfVersion")

    val guavaVersion = "33.2.0-jre"
    val assertJVersion = "3.25.3"

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("com.google.guava:guava:$guavaVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        javaParameters.set(true)
    }
}

tasks.compileTestKotlin {
    dependsOn("jandex")
}

tasks.test {
    dependsOn("jandex")
    useJUnitPlatform()
    testLogging.events = setOf(
        PASSED,
        SKIPPED,
        FAILED,
        STANDARD_OUT,
        STANDARD_ERROR
    )
}
