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
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TestMother {

    val aProject = Project("a project")

    val aTask = Task("a task")

    val someTags = listOf("onsite").map { Tag(it) }

    val aDate: LocalDate = LocalDate.of(2022, 1, 1)

    val anotherDate: LocalDate = LocalDate.of(2022, 12, 31)

    fun aTimesheetEntry() = Entry(aProject, aTask, someTags, aDate, 2.toDuration(DurationUnit.HOURS))

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

    fun aDateRange() = aDate..anotherDate

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
