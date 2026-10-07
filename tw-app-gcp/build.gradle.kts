plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.quarkus)
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(enforcedPlatform(libs.quarkus.gcp.bom))
    implementation(libs.bundles.gcp)
    implementation(libs.guava)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))

    // Quarkus 3.40.1 pins jackson-annotations to 2.21, which results in an error because JsonApplyView.class is needed, but only available from 2.22 upwards.
    // Let's fix this temporarily:
    configurations.all {
        resolutionStrategy {
            force("com.fasterxml.jackson.core:jackson-annotations:2.22")
        }
    }

    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.gcp)
    testImplementation(libs.quarkus.junit5)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}