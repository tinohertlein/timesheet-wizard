package dev.hertlein.timesheetwizard.importer.model

import dev.hertlein.timesheetwizard.importer.util.TestMother.aTimesheet
import dev.hertlein.timesheetwizard.importer.util.TestMother.aTimesheetEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Timesheet")
internal class TimesheetTest {

    @Nested
    inner class Add {

        @Test
        fun `should add entries`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy()

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).hasSize(2)
        }
    }
}
