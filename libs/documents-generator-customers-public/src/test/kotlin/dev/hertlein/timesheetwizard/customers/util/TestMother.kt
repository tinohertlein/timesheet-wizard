package dev.hertlein.timesheetwizard.customers.util

import dev.hertlein.timesheetwizard.customers.piedpiper.PdfDocumentFactory
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Project
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Task
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours

object TestMother {

    private val zoneOffset = ZoneOffset.ofHours(1)

    val aProject = Project("a project")

    val aTask = Task("a task")

    val someTags = listOf("onsite").map { Tag(it) }

    val aStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, zoneOffset)
    val anEnd = OffsetDateTime.of(2022, 1, 1, 10, 0, 0, 0, zoneOffset)

    fun aTimesheetEntry() = Entry(aProject, aTask, someTags, aStart, anEnd, 2.hours)

    fun aPdfTimesheetEntry() =
        PdfDocumentFactory.PdfTimesheetEntry(
            "2022-01-01",
            "a project",
            "onsite",
            "a task",
            "2,00"
        )

    fun anEmptyTimesheet() = Timesheet(aCustomer(), aDateRange(), emptyList())

    fun aTimesheet() = Timesheet(aCustomer(), aDateRange(), listOf(aTimesheetEntry()))

    fun aDateRange() = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

    fun aCustomer(
        id: String = "a-customer-id",
        name: String = "a-customer-name",
    ) = Customer(Id(id), Name(name))


    fun aContact(
        name: String = "a-contact-name",
        email: String = "a-contact-email"
    ) =
        ContactDetails(
            ContactDetails.Name(name),
            ContactDetails.Email(email)
        )
}
