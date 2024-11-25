
plugins {
    id("maven-publish")
    alias(libs.plugins.semver)
    alias(libs.plugins.kotlinjvm) apply false
}

group = "dev.hertlein.timesheet-wizard"
description = "The Timesheet Wizard"

version = semver.version