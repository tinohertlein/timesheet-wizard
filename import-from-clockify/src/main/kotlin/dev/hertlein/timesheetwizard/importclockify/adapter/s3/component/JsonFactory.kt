package dev.hertlein.timesheetwizard.importclockify.adapter.s3.component

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.importclockify.model.Customer
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import dev.hertlein.timesheetwizard.importclockify.model.TimesheetEntry
import io.micronaut.core.annotation.Introspected
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class JsonFactory(
    private val objectMapper: ObjectMapper
) {

    fun create(timesheet: Timesheet): String = objectMapper.writeValueAsString(TimesheetDto.of(timesheet))

    @Introspected
    data class TimesheetDto(
        val customer: CustomerDto,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val entries: List<TimesheetEntryDto>
    ) {
        companion object {
            fun of(timesheet: Timesheet) = TimesheetDto(
                CustomerDto.of(timesheet.customer),
                timesheet.dateRange.start,
                timesheet.dateRange.endInclusive,
                timesheet.entries.map { TimesheetEntryDto.of(it) }
            )
        }
    }

    @Introspected
    data class TimesheetEntryDto(
        val project: String,
        val task: String,
        val tags: List<String>,
        val date: LocalDate,
        val durationInMinutes: Long
    ) {
        companion object {
            fun of(timesheetEntry: TimesheetEntry) = TimesheetEntryDto(
                timesheetEntry.project.name,
                timesheetEntry.task.name,
                timesheetEntry.tags.map { it.name },
                timesheetEntry.date,
                timesheetEntry.duration.inWholeMinutes
            )
        }
    }

    @Introspected
    data class CustomerDto(
        val customerId: String,
        val customerName: String
    ) {
        companion object {
            fun of(customer: Customer) = CustomerDto(customer.customerId.value, customer.customerName.value)
        }
    }
}
