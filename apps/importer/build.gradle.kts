import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    val kotlin = "1.9.23"
    id("org.jetbrains.kotlin.jvm") version kotlin
    id("org.jetbrains.kotlin.kapt") version kotlin
    id("org.jetbrains.kotlin.plugin.allopen") version kotlin
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.3.4"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
}

group = "dev.hertlein.timesheetwizard"

val kotlinVersion = "1.9.23"
val javaVersion = JavaVersion.VERSION_17.toString()

repositories {
    mavenCentral()
}

dependencies {

    val guavaVersion = "33.1.0-jre"
    val kotlinLoggingVersion = "3.0.5"
    val jacksonVersion = "2.17.0"
    val logbackVersion = "1.5.3"
    val awsSdkVersion = "2.25.9"
    val awsLambdaVersion = "3.11.4"

    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    kapt("io.micronaut:micronaut-http-validation")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")
    implementation(platform("software.amazon.awssdk:bom:$awsSdkVersion"))
    implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaVersion")
    implementation("software.amazon.awssdk:s3")

    val junitVersion = "5.10.2"
    val mockServerVersion = "5.15.0"
    val testContainersVersion = "1.19.7"
    val assertJVersion = "3.25.3"
    val mockkVersion = "1.13.10"

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.mock-server:mockserver-netty:$mockServerVersion")
    testImplementation("org.mock-server:mockserver-client-java:$mockServerVersion")
    testImplementation("org.testcontainers:mockserver")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("org.yaml:snakeyaml")
    compileOnly("org.graalvm.nativeimage:svm")
}

buildscript {
    dependencies {
        classpath("com.github.docker-java:docker-java-transport-httpclient5:3.3.6") {
            because("M1 macs need a later version of JNA")
        }
    }
}

application {
    mainClass.set("dev.hertlein.timesheetwizard.importer.adapter.lambda.LambdaAdapter")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}


tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
    kotlinOptions.javaParameters = true
}

micronaut {
    runtime("lambda_provided")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("dev.hertlein.timesheetwizard.importer.*")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    baseImage.set("public.ecr.aws/amazonlinux/amazonlinux:2")
    args(
        "-XX:MaximumHeapSizePercent=80",
        "-Dio.netty.allocator.numDirectArenas=0",
        "-Dio.netty.noPreferDirect=true",
        "-march=compatibility"
    )
}

tasks.test {
    useJUnitPlatform()
    testLogging.events = setOf(PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR)
}
