package dev.hertlein.timesheetwizard.generateexports.model

import dev.hertlein.timesheetwizard.generateexports.util.TestMother
import dev.hertlein.timesheetwizard.generateexports.util.TestMother.aCustomer
import dev.hertlein.timesheetwizard.generateexports.util.TestMother.aDateRange
import dev.hertlein.timesheetwizard.generateexports.util.TestMother.anEmptyTimesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("Timesheet")
internal class TimesheetTest {

    @Nested
    inner class TotalDuration {

        @Test
        fun `should sum up duration of all entries`() {
            val entries = (1..10).toList().map {
                TimesheetEntry(
                    TestMother.aProject,
                    TestMother.aTask,
                    TestMother.someTags,
                    TestMother.aDate,
                    (it.toDuration(DurationUnit.HOURS))
                )
            }
            val timesheet = Timesheet(aCustomer(), aDateRange(), entries)

            val totalDuration = timesheet.totalDuration()

            assertThat(totalDuration).isEqualTo((55.toDuration(DurationUnit.HOURS)))
        }

        @Test
        fun `should sum up duration if no entries present`() {
            val totalDuration = anEmptyTimesheet().totalDuration()

            assertThat(totalDuration).isEqualTo((0.toDuration(DurationUnit.HOURS)))
        }
    }
}
