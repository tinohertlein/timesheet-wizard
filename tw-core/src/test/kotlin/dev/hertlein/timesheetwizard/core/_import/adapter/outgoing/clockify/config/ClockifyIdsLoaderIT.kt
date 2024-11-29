package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.TestApplication
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("ClockifyIdsLoader")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
internal class ClockifyIdsLoaderIT {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var clockifyIdsLoader: ClockifyIdsLoader

    @BeforeEach
    fun setup() {
        cloudPersistence.upload(
            "config/clockify.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/clockify.json")
        )
    }

    @Test
    fun `should load clockify ids`() {
        val clockifyIds = clockifyIdsLoader.loadClockifyIds()

        Assertions.assertThat(clockifyIds).containsExactly(ClockifyId("1000", "62dd35202849d633796f5459"))
    }
}