package dev.hertlein.timesheetwizard.importclockify.util

import dev.hertlein.timesheetwizard.importclockify.model.Customer
import dev.hertlein.timesheetwizard.importclockify.model.Project
import dev.hertlein.timesheetwizard.importclockify.model.Tag
import dev.hertlein.timesheetwizard.importclockify.model.Task
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import dev.hertlein.timesheetwizard.importclockify.model.TimesheetEntry
import java.time.LocalDate
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
          "date": [
            2022,
            1,
            1
          ],
          "durationInMinutes": 120
        }
      ]
    }
    """.trimIndent()

    fun aTimesheetEntry() = TimesheetEntry(aProject, aTask, someTags, aDate, 2.toDuration(DurationUnit.HOURS))

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange()).add(aTimesheetEntry())

    fun aDateRange() = aDate..anotherDate

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
        clockifyId: String = "a-clockify-id"
    ) = Customer.of(id, name, clockifyId)

}
