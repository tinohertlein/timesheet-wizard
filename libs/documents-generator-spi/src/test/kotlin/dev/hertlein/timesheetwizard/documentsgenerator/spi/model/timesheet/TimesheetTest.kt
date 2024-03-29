package dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet

import dev.hertlein.timesheetwizard.documentsgenerator.spi.util.TestMother
import dev.hertlein.timesheetwizard.documentsgenerator.spi.util.TestMother.aCustomer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.util.TestMother.aDateRange
import dev.hertlein.timesheetwizard.documentsgenerator.spi.util.TestMother.anEmptyTimesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.hours

@DisplayName("Timesheet")
internal class TimesheetTest {

    @Nested
    inner class TotalDuration {

        @Test
        fun `should sum up duration of all entries`() {
            val entries = (1..10).toList().map {
                Timesheet.Entry(
                    TestMother.aProject,
                    TestMother.aTask,
                    TestMother.someTags,
                    TestMother.aStart,
                    TestMother.anEnd,
                    (it.hours)
                )
            }
            val timesheet = Timesheet(aCustomer(), aDateRange, entries)

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
