package dev.hertlein.timesheetwizard.documentsgenerator.util

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Project
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Task
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestMother {

    val aProject = Project("a project")

    val aTask = Task("a task")

    val someTags = listOf("onsite").map { Tag(it) }

    val aStart = LocalDateTime.of(2022, 1, 1, 8, 0, 0)
    val anEnd = LocalDateTime.of(2022, 1, 1, 10, 0, 0)

    val emptyTimesheetJson = """{
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
      ]
    }""".trimIndent()

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
            0,
            0
          ],
           "end": [
            2022,
            1,
            1,
            10,
            0,
            0
          ],
          "durationInMinutes": 120
        }
      ]
    }
    """.trimIndent()

    fun aTimesheetEntry() = Entry(aProject, aTask, someTags, aStart, anEnd, 2.toDuration(DurationUnit.HOURS))

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange(), listOf(aTimesheetEntry()))

    fun aDateRange() = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(Id(id), Name(name))

}
