package dev.hertlein.timesheetwizard.util

import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.Customer.Id
import dev.hertlein.timesheetwizard.shared.model.Customer.Name
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours

object TestMother {
    fun aCustomer() = Customer(Id("a-customer-id"), Name("a-customer-name"), true)

    val aZoneOffset = ZoneOffset.ofHours(1)

    val aProject = Timesheet.Entry.Project("a-project")

    val aTask = Timesheet.Entry.Task("a-task")

    val someTags = listOf("a-tag").map { Timesheet.Entry.Tag(it) }

    val aStart: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)

    val anEnd: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 10, 0, 0, 0, aZoneOffset)

    val aDateRange = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

    val aDateTimeRange = Timesheet.Entry.DateTimeRange(aStart, anEnd)

    val anEntry =
        Timesheet.Entry(
            aProject,
            aTask,
            someTags,
            aDateTimeRange,
            2.hours
        )

    val anEmptyTimesheet = Timesheet(aCustomer(), aDateRange, emptyList())
}