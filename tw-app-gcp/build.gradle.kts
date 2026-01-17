plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.quarkus)
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(enforcedPlatform(libs.quarkus.gcp.bom))
    implementation(libs.bundles.quarkus.base)
    implementation(libs.bundles.quarkus.gcp)
    implementation(libs.guava)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))

    testImplementation(libs.bundles.testing)
    testImplementation(libs.bundles.testing.gcp)
    testImplementation(libs.quarkus.junit5)
    testImplementation(testFixtures(project(":tw-core")))
}

version = "dummy"

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