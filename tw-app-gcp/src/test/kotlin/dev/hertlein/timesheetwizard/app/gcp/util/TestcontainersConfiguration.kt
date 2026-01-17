package dev.hertlein.timesheetwizard.app.gcp.util

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class TestcontainersConfiguration : QuarkusTestResourceLifecycleManager {

    private val gcpStorageContainer = GenericContainer(DockerImageName.parse("oittaa/gcp-storage-emulator:latest"))

    override fun start(): Map<String, String> {
        gcpStorageContainer.apply {
            withCommand("start --port=9023 --in-memory")
            addExposedPort(9023)
            start()
        }
        return mapOf("quarkus.google.cloud.storage.host-override" to "http://${gcpStorageContainer.host}:${gcpStorageContainer.firstMappedPort}")
    }

    override fun stop() {
        gcpStorageContainer.stop()
    }
}
