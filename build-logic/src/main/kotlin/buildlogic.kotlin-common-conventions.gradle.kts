import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("org.jetbrains.kotlin.jvm") apply false
    jacoco
}

repositories {
    mavenLocal()
    mavenCentral()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        javaParameters.set(true)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.jacocoTestReport {
    reports {
        csv.required = false
        xml.required = true
        html.required = true
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    testLogging.events = setOf(
        PASSED,
        SKIPPED,
        FAILED,
        STANDARD_OUT,
        STANDARD_ERROR
    )
}

