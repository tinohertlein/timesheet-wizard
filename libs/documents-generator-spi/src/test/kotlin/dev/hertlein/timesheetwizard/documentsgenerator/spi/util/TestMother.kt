package dev.hertlein.timesheetwizard.documentsgenerator.spi.util

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import java.time.LocalDate
import java.time.LocalDateTime

object TestMother {

    val aProject = Timesheet.Entry.Project("a project")

    val aTask = Timesheet.Entry.Task("a task")

    val someTags = listOf("onsite").map { Timesheet.Entry.Tag(it) }

    val aStart: LocalDateTime = LocalDateTime.of(2022, 1, 1, 8, 0, 0)

    val anEnd: LocalDateTime = LocalDateTime.of(2022, 1, 1, 10, 0, 0)

    val aDateRange = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

    val anEmptyTimesheet = Timesheet(aCustomer(), aDateRange, emptyList())

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(Id(id), Name(name))

}
