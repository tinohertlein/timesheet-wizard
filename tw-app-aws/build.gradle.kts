import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer

plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spring.thin)
    alias(libs.plugins.shadow)
}

setProperty("mainClassName", "dev.hertlein.timesheetwizard.core.TwApplication")

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

    testImplementation(libs.bundles.testing)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}

tasks.assemble {
    dependsOn("shadowJar")
}

tasks.bootStartScripts {
    dependsOn("jar")
}

tasks.thinJar {
    archiveFileName.set("timesheet-wizard-aws-thin.jar")
}

tasks.shadowJar {
    dependsOn("thinJar")
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
    archiveFileName.set("timesheet-wizard-aws.jar")
}