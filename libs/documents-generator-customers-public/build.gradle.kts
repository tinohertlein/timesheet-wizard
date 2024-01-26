import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("org.kordamp.gradle.jandex") version "1.1.0"
}

group = "dev.hertlein.timesheetwizard"
version = "1.0.2"

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
    val twSpiVersion = "1.0.1"
    val jakartaApiVersion = "2.0.1"
    val openCsvVersion = "5.9"
    val poiVersion = "5.2.5"
    val jasperVersion = "6.21.0"
    val openPdfVersion = "1.3.39"

    implementation("dev.hertlein.timesheetwizard:documents-generator-spi:$twSpiVersion")
    implementation("jakarta.inject:jakarta.inject-api:$jakartaApiVersion")
    implementation("com.opencsv:opencsv:$openCsvVersion")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("net.sf.jasperreports:jasperreports:$jasperVersion")
    implementation("com.github.librepdf:openpdf:$openPdfVersion")

    val guavaVersion = "33.0.0-jre"
    val assertJVersion = "3.25.2"

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("com.google.guava:guava:$guavaVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.javaParameters = true
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
