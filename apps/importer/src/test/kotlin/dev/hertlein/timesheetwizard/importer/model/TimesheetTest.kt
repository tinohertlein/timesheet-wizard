package dev.hertlein.timesheetwizard.importer.model

import dev.hertlein.timesheetwizard.importer.util.TestMother.aTimesheet
import dev.hertlein.timesheetwizard.importer.util.TestMother.aTimesheetEntry
import dev.hertlein.timesheetwizard.importer.util.TestMother.anotherDate
import dev.hertlein.timesheetwizard.importer.util.TestMother.anotherProject
import dev.hertlein.timesheetwizard.importer.util.TestMother.anotherTask
import dev.hertlein.timesheetwizard.importer.util.TestMother.someOtherTags
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Timesheet")
internal class TimesheetTest {

    @Nested
    inner class Add {

        @Test
        fun `should add and aggregate entries`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy()

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).hasSize(1)
            assertThat(timesheetAfterAdd.entries[0].duration).isEqualTo(anEntry.duration + anotherEntry.duration)
        }

        @Test
        fun `should add and not aggregate entries with differing projects`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy(project = anotherProject)

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).containsExactlyInAnyOrder(anEntry, anotherEntry)
        }

        @Test
        fun `should add and not aggregate entries with differing tasks`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy(task = anotherTask)

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).containsExactlyInAnyOrder(anEntry, anotherEntry)
        }

        @Test
        fun `should add and not aggregate entries with differing tags`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy(tags = someOtherTags)

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).containsExactlyInAnyOrder(anEntry, anotherEntry)
        }

        @Test
        fun `should add and not aggregate entries with differing dates`() {
            val anEntry = aTimesheetEntry()
            val anotherEntry = anEntry.copy(date = anotherDate)

            val timesheetAfterAdd = aTimesheet().add(anotherEntry)

            assertThat(timesheetAfterAdd.entries).containsExactlyInAnyOrder(anEntry, anotherEntry)
        }
    }
}
