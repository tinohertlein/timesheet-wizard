import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
}

group = "dev.hertlein.timesheetwizard"
version = "2.0.4"

repositories {
    mavenCentral()
    mavenLocal()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tinohertlein/timesheet-wizard")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
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
    val assertJVersion = "3.25.3"

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileKotlin {
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
