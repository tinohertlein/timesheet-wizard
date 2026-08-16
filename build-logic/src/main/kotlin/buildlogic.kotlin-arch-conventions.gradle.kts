plugins {
    id("buildlogic.kotlin-common-conventions")
}

testing {
    suites {
        register<JvmTestSuite>("archTest") {
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
    dependsOn(testing.suites.named("archTest"))
}
