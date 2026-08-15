
plugins {
    id("maven-publish")
    alias(libs.plugins.semver)
    alias(libs.plugins.kotlinjvm) apply false
    id("com.hello2morrow.sonargraph") version "26.4.1"
}

group = "dev.hertlein.timesheet-wizard"
description = "The Timesheet Wizard"

version = semver.version

sonargraph {
    autoUpdate = true
}