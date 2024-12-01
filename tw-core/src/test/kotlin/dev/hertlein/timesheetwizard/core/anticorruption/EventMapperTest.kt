package dev.hertlein.timesheetwizard.core.anticorruption

import dev.hertlein.timesheetwizard.core._import.domain.model.Customer
import dev.hertlein.timesheetwizard.core._import.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("EventMapper")
class EventMapperTest {

    private val aZoneOffset = ZoneOffset.ofHours(1)

    private val aStart: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)
    private val anEnd: OffsetDateTime = aStart.plusHours(1)

    private val aDateRange = aStart.toLocalDate()  ..anEnd.toLocalDate()

    private val aCustomerId = "a-customer-id"
    private val aCustomerName = "a-customer-name"

    private val aProject = "a-project"
    private val aTask = "a-task"
    private val someTags = listOf("a-tag")

    private val applicationEventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
    private val eventMapper = EventMapper(applicationEventPublisher)

    @BeforeEach
    fun setup() {
        every { applicationEventPublisher.publishEvent(any()) } just Runs
    }

    @Test
    fun `should map importTimesheet to exportTimeSheet`() {
        val importEntry = ImportTimesheet.Entry.of(
            aProject,
            aTask,
            someTags,
            aStart,
            anEnd,
            2.toDuration(DurationUnit.HOURS)
        )
        val exportEntry = ExportTimesheet.Entry.of(
            aProject,
            aTask,
            someTags,
            aStart,
            anEnd,
            2.toDuration(DurationUnit.HOURS)
        )
        val importTimesheet =
            ImportTimesheet(Customer.of(aCustomerId, aCustomerName), aDateRange, listOf(importEntry))
        val exportTimesheet =
            ExportTimesheet(ExportTimesheet.Customer(aCustomerId, aCustomerName), aDateRange, listOf(exportEntry))

        eventMapper.onTimesheetImported(importTimesheet)

        verify { applicationEventPublisher.publishEvent(exportTimesheet) }
        confirmVerified(applicationEventPublisher)
    }
}