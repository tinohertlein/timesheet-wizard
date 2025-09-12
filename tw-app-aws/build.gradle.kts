import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer

plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.shadow)
    alias(libs.plugins.javaagent.test)
}


dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi-cloud"))
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.web)
    implementation(platform(libs.spring.cloud))
    implementation(libs.bundles.spring.cloud)
    implementation(platform(libs.spring.cloud.aws))
    implementation(libs.bundles.spring.cloud.aws)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.aws)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.jar {
    enabled = false
    archiveClassifier = ""

}

tasks.bootJar {
    archiveFileName.set("tw-app-aws.jar")
    layered {
        enabled = true
    }
}

tasks.shadowJar {
    dependsOn("jar")
    manifest.inheritFrom(project.tasks.jar.get().manifest)
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
    archiveFileName.set("timesheet-wizard-aws-shadow.jar")
}

tasks.assemble {
    dependsOn("shadowJar")
}