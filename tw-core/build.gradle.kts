plugins {
    id("buildlogic.kotlin-library-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi-cloud"))
    implementation(libs.bundles.spring.web)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    implementation(libs.bundles.export)

    testImplementation(libs.bundles.testing)
    testFixturesApi(libs.bundles.testing)
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}