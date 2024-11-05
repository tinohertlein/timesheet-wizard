
plugins {
    id("maven-publish")
    alias(libs.plugins.semver)
}

group = "dev.hertlein.timesheet-wizard"
description = "The Timesheet Wizard"

version = semver.version