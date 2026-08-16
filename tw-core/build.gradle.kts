plugins {
    id("buildlogic.kotlin-library-conventions")
    id("buildlogic.kotlin-e2e-conventions")
    id("buildlogic.kotlin-arch-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.javaagent.test)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":tw-spi"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    implementation(libs.bundles.export)

    testJavaagent(libs.byte.buddy.agent)
    testRuntimeOnly(libs.bundles.testing.core)
    testFixturesApi(libs.bundles.testing)
    testFixturesApi(libs.kotlin.logging)
    testFixturesImplementation(project(":tw-spi"))

    e2eTestImplementation(project())
    e2eTestImplementation(testFixtures(project()))
    e2eTestImplementation(libs.bundles.cucumber)
    e2eTestRuntimeOnly(libs.bundles.testing.core)
    e2eTestRuntimeOnly(libs.junit.platform.launcher)

    archTestImplementation(project())
    archTestImplementation(libs.archunit)
    archTestImplementation(libs.kotlin.junit)
    archTestRuntimeOnly(libs.junit.platform.launcher)
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}