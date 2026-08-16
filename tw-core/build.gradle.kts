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
}

tasks.compileJava {
    options.compilerArgs.add("-parameters")
}

testing {
    suites {
        register<JvmTestSuite>("archTest") {
            useJUnitJupiter()

            dependencies {
                implementation(project())
                implementation(libs.archunit)
                implementation(libs.kotlin.junit)
                runtimeOnly(libs.junit.platform.launcher)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.test)
                    }
                }
            }
        }
    }
}

tasks.check {
    dependsOn(testing.suites.named("archTest"))
}