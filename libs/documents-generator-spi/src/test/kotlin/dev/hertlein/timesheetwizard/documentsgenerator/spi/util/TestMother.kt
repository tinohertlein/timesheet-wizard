package dev.hertlein.timesheetwizard.documentsgenerator.spi.util

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

object TestMother {

    private val zoneOffset = ZoneOffset.ofHours(1)

    val aProject = Timesheet.Entry.Project("a project")

    val aTask = Timesheet.Entry.Task("a task")

    val someTags = listOf("onsite").map { Timesheet.Entry.Tag(it) }

    val aStart: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, zoneOffset)

    val anEnd: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 10, 0, 0, 0, zoneOffset)

    val aDateRange = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

    val anEmptyTimesheet = Timesheet(aCustomer(), aDateRange, emptyList())

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(Id(id), Name(name))

}
