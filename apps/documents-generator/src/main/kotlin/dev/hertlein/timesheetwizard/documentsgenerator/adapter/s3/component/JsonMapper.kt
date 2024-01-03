package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@ApplicationScoped
class JsonMapper(
    private val objectMapper: ObjectMapper
) {

    fun toTimesheetEntity(json: String): Timesheet = objectMapper.readValue(json, TimesheetDto::class.java).toEntity()

    @RegisterForReflection
    data class TimesheetDto(
        val customer: CustomerDto? = null,
        val startDate: LocalDate? = null,
        val endDate: LocalDate? = null,
        val entries: List<TimesheetEntryDto>? = listOf()
    ) {
        fun toEntity() = Timesheet(
            customer!!.toEntity(),
            startDate!!..endDate!!,
            entries!!.map { it.toEntity() }
        )
    }

    @RegisterForReflection
    data class TimesheetEntryDto(
        val project: String? = null,
        val task: String? = null,
        val tags: List<String>? = listOf(),
        val date: LocalDate? = null,
        val durationInMinutes: Long? = null
    ) {
        fun toEntity() = Timesheet.Entry(
            Timesheet.Entry.Project(project!!),
            Timesheet.Entry.Task(task ?: ""),
            tags!!.map { Timesheet.Entry.Tag(it) },
            date!!,
            durationInMinutes!!.toDuration(DurationUnit.MINUTES)
        )
    }

    @RegisterForReflection
    data class CustomerDto(
        val customerId: String? = null,
        val customerName: String? = null
    ) {
        fun toEntity() = Customer(Id(customerId!!), Name(customerName!!))
    }
}
