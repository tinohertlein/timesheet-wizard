package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.util.TestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ClockifyIdsLoader")
internal class ClockifyIdsLoaderTest {

    private val cloudPersistence = TestFixture.App.cloudPersistenceInMemory
    private val clockifyIdsLoader: ClockifyIdsLoader = ClockifyIdsLoader(cloudPersistence, TestFixture.App.objectMapper)

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

        assertThat(clockifyIds).containsExactly(ClockifyId("1000", "62dd35202849d633796f5459"))
    }
}