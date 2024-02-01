package dev.hertlein.timesheetwizard.importer.util

import dev.hertlein.timesheetwizard.importer.model.Project
import dev.hertlein.timesheetwizard.importer.model.Task
import dev.hertlein.timesheetwizard.importer.model.Tag
import dev.hertlein.timesheetwizard.importer.model.Customer
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import dev.hertlein.timesheetwizard.importer.model.TimesheetEntry
import dev.hertlein.timesheetwizard.importer.model.DateTimeRange
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestMother {

    val aProject = Project("a project")
    val anotherProject = Project("another project")

    val aTask = Task("a task")
    val anotherTask = Task("another task")

    val someTags = listOf("onsite").map { Tag(it) }
    val someOtherTags = listOf("remote").map { Tag(it) }

    val aDate = LocalDate.of(2022, 1, 1)
    val anotherDate = LocalDate.of(2022, 12, 31)

    val aDateTimeStart = LocalDateTime.of(2022, 1, 1, 8, 0, 0)
    val aDateTimeEnd = LocalDateTime.of(2022, 1, 1, 9, 0, 0)
    val anotherDateTimeStart = LocalDateTime.of(2022, 12, 31, 10, 0, 0)
    val anotherDateTimeEnd = LocalDateTime.of(2022, 12, 31, 11, 0, 0)

    val timesheetJson = """{
      "customer": {
        "customerId": "a-customer-id",
        "customerName": "a-customer-name"
      },
      "startDate": [
        2022,
        1,
        1
      ],
      "endDate": [
        2022,
        12,
        31
      ],
      "entries": [
        {
          "project": "a project",
          "task": "a task",
          "tags": [
            "onsite"
          ],
          "start": [
            2022,
            1,
            1,
            8,
            0
          ],
          "end": [
            2022,
            1,
            1,
            9,
            0
          ],
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
            1.toDuration(DurationUnit.HOURS)
        )

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange()).add(aTimesheetEntry())

    fun aDateRange() = aDate..anotherDate

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
        clockifyId: String = "a-clockify-id"
    ) = Customer.of(id, name, clockifyId)

}
