plugins {
    id("buildlogic.kotlin-application-conventions")
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.javaagent.test)
    alias(libs.plugins.shadow)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":tw-spi"))
    implementation(project(":tw-core"))
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.logging)
    implementation(libs.guava)
    implementation(libs.bundles.cli)

    testJavaagent(libs.byte.buddy.agent)
    testImplementation(libs.bundles.testing)
    testImplementation(testFixtures(project(":tw-core")))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.hertlein.timesheetwizard.app.local.LocalApplicationKt"
    }
}

tasks.shadowJar {
    // There are multiple 'jasperreports_extension.properties' in Jasper jar files. All of them are needed to create a PDF report. 
    // The default duplicatesStrategy (DuplicatesStrategy.EXCLUDE) is merging only the first one into the final jar, which results in a runtime exception when generating PDF reports later on.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}