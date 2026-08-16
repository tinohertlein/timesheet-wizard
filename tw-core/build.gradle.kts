plugins {
    id("buildlogic.kotlin-library-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.javaagent.test)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    implementation(libs.bundles.export)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.cucumber)
    testRuntimeOnly(libs.bundles.testing.core)
    testFixturesApi(libs.bundles.testing)
    testFixturesImplementation(project(":tw-spi"))

    archTestImplementation(libs.archunit)
    archTestImplementation(libs.kotlin.junit)
    archTestRuntimeOnly(libs.junit.platform.launcher)
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}