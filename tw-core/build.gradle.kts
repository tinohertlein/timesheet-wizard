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
    testFixturesApi(libs.bundles.testing)
    testFixturesImplementation(project(":tw-spi"))
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}