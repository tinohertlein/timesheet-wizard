plugins {
    id("buildlogic.kotlin-common-conventions")
}

testing {
    suites {
        register<JvmTestSuite>("e2eTest") {
            useJUnitJupiter()

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
    dependsOn(testing.suites.named("e2eTest"))
}
