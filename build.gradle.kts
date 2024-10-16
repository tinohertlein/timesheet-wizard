import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    val kotlin = "2.0.21"
    kotlin("jvm") version kotlin
    kotlin("plugin.allopen") version kotlin
    kotlin("plugin.spring") version kotlin
    id("maven-publish")
    id("com.github.jmongard.git-semver-plugin") version "0.12.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springframework.boot") version "3.3.4"
    id("org.springframework.boot.experimental.thin-launcher") version "1.0.31.RELEASE"
}

group = "dev.hertlein.timesheet-wizard"
version = semver.version
description = "The Timesheet Wizard"

val springCloudVersion = "2023.0.3"

tasks.assemble {
    dependsOn("thinJar", "shadowJar")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.cloud:spring-cloud-function-context")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws")

    val springCloudAwsVersion = "3.2.0"
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${springCloudAwsVersion}"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")


    val guavaVersion = "33.3.1-jre"
    val kotlinLoggingVersion = "3.0.5"
    val jacksonKotlinVersion = "2.17.2"

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("com.google.guava:guava:$guavaVersion")

    val openCsvVersion = "5.9"
    val poiVersion = "5.3.0"
    val jasperVersion = "7.0.1"
    val openPdfVersion = "2.0.3"

    implementation("com.opencsv:opencsv:$openCsvVersion")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("net.sf.jasperreports:jasperreports:$jasperVersion")
    implementation("net.sf.jasperreports:jasperreports-pdf:$jasperVersion")
    implementation("net.sf.jasperreports:jasperreports-jdt:$jasperVersion")
    implementation("com.github.librepdf:openpdf:$openPdfVersion")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    val mockkVersion = "1.13.13"
    val mockServerVersion = "5.15.0"

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.mock-server:mockserver-netty:$mockServerVersion")
    testImplementation("org.mock-server:mockserver-client-java:$mockServerVersion")
    testImplementation("org.testcontainers:mockserver")
    testImplementation("org.testcontainers:localstack")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        javaParameters.set(true)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
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

tasks.shadowJar {
    mustRunAfter("thinJar")
    manifest.inheritFrom(project.tasks.thinJar.get().manifest)
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring.tooling")
    append("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
    append("META-INF/spring/org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration.imports")

    transform(PropertiesFileTransformer::class.java) {
        paths = mutableListOf("META-INF/spring.factories")
        mergeStrategy = "append"
    }
    archiveFileName.set("timesheet-wizard.jar")
}