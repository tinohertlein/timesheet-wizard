package dev.hertlein.timesheetwizard.generateexports.util

import dev.hertlein.timesheetwizard.generateexports.application.factory.PDFDocumentFactory
import dev.hertlein.timesheetwizard.generateexports.model.Customer
import dev.hertlein.timesheetwizard.generateexports.model.CustomerId
import dev.hertlein.timesheetwizard.generateexports.model.CustomerName
import dev.hertlein.timesheetwizard.generateexports.model.Project
import dev.hertlein.timesheetwizard.generateexports.model.Tag
import dev.hertlein.timesheetwizard.generateexports.model.Task
import dev.hertlein.timesheetwizard.generateexports.model.Timesheet
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetEntry
import java.time.LocalDate
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestMother {

    val aProject = Project("a project")

    val aTask = Task("a task")

    val someTags = listOf("onsite").map { Tag(it) }

    val aDate: LocalDate = LocalDate.of(2022, 1, 1)
    val anotherDate: LocalDate = LocalDate.of(2022, 12, 31)

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

    fun aPDFTimesheetEntry() =
        PDFDocumentFactory.PDFTimesheetEntry(
            "2022-01-01",
            "a project",
            "onsite",
            "a task",
            "2,00"
        )

    fun anEmptyTimesheet() = Timesheet(aCustomer(), aDateRange(), emptyList())

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange(), listOf(aTimesheetEntry()))

    fun aDateRange() = aDate..anotherDate

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(CustomerId(id), CustomerName(name))

}
