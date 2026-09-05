plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.javaagent.test)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))
    implementation(libs.bundles.spring.base)
    implementation(libs.bundles.scaleway)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.scaleway)
    testImplementation(libs.spring.starter.test)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.withType<GenerateModuleMetadata> {
    suppressedValidationErrors.add("enforced-platform")
}