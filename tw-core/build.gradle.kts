plugins {
    id("buildlogic.kotlin-library-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.javaagent.test)
}

dependencyManagement {
    imports {
        mavenBom(libs.spring.boot.dependencies.get().toString()) {
            bomProperty("kotlin.version", libs.plugins.kotlinjvm.get().version.toString())
        }
    }
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi-cloud"))
    implementation(libs.bundles.spring.base)
    implementation(platform(libs.spring.modulith))
    implementation(libs.spring.modulith.starter.core)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    implementation(libs.bundles.export)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.spring.modulith.starter.test)
    testFixturesApi(libs.bundles.testing)
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}