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

    finalizedBy(tasks.jacocoTestReport)

    testLogging.events = setOf(
        PASSED,
        SKIPPED,
        FAILED,
        STANDARD_OUT,
        STANDARD_ERROR
    )
}

