package dev.hertlein.timesheetwizard.core.export.core.model

import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.anEntry
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aTimesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.hours

@DisplayName("Timesheet")
internal class ExportTimesheetTest {

    @Nested
    inner class TotalDuration {

        @Test
        fun `should sum up duration of all entries`() {
            val entries = (1..10).map {
                anEntry.copy(duration = it.hours)
            }
            val timesheet = aTimesheet(entries)

            val totalDuration = timesheet.totalDuration()

            assertThat(totalDuration).isEqualTo((55.hours))
        }

        @Test
        fun `should sum up duration if no entries present`() {
            val totalDuration = aTimesheet(emptyList()).totalDuration()

            assertThat(totalDuration).isEqualTo((0.hours))
        }
    }
}
