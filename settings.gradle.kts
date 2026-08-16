pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "timesheet-wizard"
// Do not include 'tw-app-azure', as spring-boot-thin-launcher Gradle plugin does not work with Gradle > 9.0
include("tw-spi", "tw-core", "tw-app-aws", "tw-app-gcp", "tw-app-local")
