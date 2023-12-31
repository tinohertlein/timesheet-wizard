package dev.hertlein.timesheetwizard.documentsgenerator.spi.util

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.CustomerId
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.CustomerName
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Project
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Task
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.TimesheetEntry
import java.time.LocalDate
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestMother {

    val aProject = Project("a project")

    val aTask = Task("a task")

    val someTags = listOf("onsite").map { Tag(it) }

    val aDate: LocalDate = LocalDate.of(2022, 1, 1)
    val anotherDate: LocalDate = LocalDate.of(2022, 12, 31)


    fun aTimesheetEntry() = TimesheetEntry(aProject, aTask, someTags, aDate, 2.toDuration(DurationUnit.HOURS))

    fun anEmptyTimesheet() = Timesheet(aCustomer(), aDateRange(), emptyList())

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange(), listOf(aTimesheetEntry()))

    fun aDateRange() = aDate..anotherDate

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(CustomerId(id), CustomerName(name))

}
