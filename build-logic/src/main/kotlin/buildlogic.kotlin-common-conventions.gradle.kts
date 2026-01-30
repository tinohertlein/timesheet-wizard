import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
import org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT

plugins {
    id("org.jetbrains.kotlin.jvm") apply false
    jacoco
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        javaParameters.set(true)
        freeCompilerArgs.addAll("-Xjsr305=strict")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
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
    // https://junit-pioneer.org/docs/environment-variables/#warnings-for-reflective-access
    jvmArgs("--add-opens", "java.base/java.util=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")

    useJUnitPlatform()

    finalizedBy(tasks.jacocoTestReport)

    testLogging.events = setOf(
        PASSED,
        SKIPPED,
        FAILED,
        STANDARD_OUT,
        STANDARD_ERROR
    )
}