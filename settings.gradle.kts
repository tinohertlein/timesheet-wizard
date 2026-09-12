pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        kotlin("kapt") version "2.4.10"
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "timesheet-wizard"
include("tw-spi", "tw-core", "tw-app-aws", "tw-app-azure", "tw-app-gcp", "tw-app-local", "tw-app-scaleway")
