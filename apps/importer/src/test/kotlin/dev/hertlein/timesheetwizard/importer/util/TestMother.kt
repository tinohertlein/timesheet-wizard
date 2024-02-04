package dev.hertlein.timesheetwizard.importer.util

import dev.hertlein.timesheetwizard.importer.model.Customer
import dev.hertlein.timesheetwizard.importer.model.DateTimeRange
import dev.hertlein.timesheetwizard.importer.model.Project
import dev.hertlein.timesheetwizard.importer.model.Tag
import dev.hertlein.timesheetwizard.importer.model.Task
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import dev.hertlein.timesheetwizard.importer.model.TimesheetEntry
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours

object TestMother {
    private val zoneOffset = ZoneOffset.ofHours(1)

    val aProject = Project("a project")
    val anotherProject = Project("another project")

    val aTask = Task("a task")
    val anotherTask = Task("another task")

    val someTags = listOf("onsite").map { Tag(it) }
    val someOtherTags = listOf("remote").map { Tag(it) }

    val aDate = LocalDate.of(2022, 1, 1)
    val anotherDate = LocalDate.of(2022, 12, 31)

    val aDateTimeStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, zoneOffset)
    val aDateTimeEnd = OffsetDateTime.of(2022, 1, 1, 9, 0, 0,0, zoneOffset)

    val timesheetJson = """{
      "customer": {
        "customerId": "a-customer-id",
        "customerName": "a-customer-name"
      },
      "startDate": "2022-01-01",
      "endDate": "2022-12-31",
      "entries": [
        {
          "project": "a project",
          "task": "a task",
          "tags": [
            "onsite"
          ],
          "start": "2022-01-01T08:00:00+01:00",
          "end": "2022-01-01T09:00:00+01:00",
          "durationInMinutes": 60
        }
      ]
    }
    """.trimIndent()

    fun aTimesheetEntry() =
        TimesheetEntry(
            aProject,
            aTask,
            someTags,
            DateTimeRange(aDateTimeStart, aDateTimeEnd),
            1.hours
        )

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange()).add(aTimesheetEntry())

    fun aDateRange() = aDate..anotherDate

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
        clockifyId: String = "a-clockify-id"
    ) = Customer.of(id, name, clockifyId)

}
