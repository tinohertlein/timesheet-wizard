package dev.hertlein.timesheetwizard.core.shared.configloader.model

import dev.hertlein.timesheetwizard.core.util.TestFixture.anEmptyTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.anEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.hours

@DisplayName("Timesheet")
internal class TimesheetTest {

    @Nested
    inner class Add {

        @Test
        fun `should add entries`() {
            val timesheetAfterAdd = anEmptyTimesheet.add(anEntry)

            assertThat(timesheetAfterAdd.entries).hasSize(1)
        }
    }

    @Nested
    inner class TotalDuration {

        @Test
        fun `should sum up duration of all entries`() {
            val entries = (1..10).map {
                anEntry.copy(duration = it.hours)
            }
            val timesheet = anEmptyTimesheet.copy(entries = entries)

            val totalDuration = timesheet.totalDuration()

            assertThat(totalDuration).isEqualTo((55.hours))
        }

        @Test
        fun `should sum up duration if no entries present`() {
            val totalDuration = anEmptyTimesheet.totalDuration()

            assertThat(totalDuration).isEqualTo((0.hours))
        }
    }
}
