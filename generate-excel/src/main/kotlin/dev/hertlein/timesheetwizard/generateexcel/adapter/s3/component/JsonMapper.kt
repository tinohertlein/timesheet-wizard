package dev.hertlein.timesheetwizard.generateexcel.adapter.s3.component

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.generateexcel.model.Customer
import dev.hertlein.timesheetwizard.generateexcel.model.CustomerId
import dev.hertlein.timesheetwizard.generateexcel.model.CustomerName
import dev.hertlein.timesheetwizard.generateexcel.model.Project
import dev.hertlein.timesheetwizard.generateexcel.model.Tag
import dev.hertlein.timesheetwizard.generateexcel.model.Task
import dev.hertlein.timesheetwizard.generateexcel.model.Timesheet
import dev.hertlein.timesheetwizard.generateexcel.model.TimesheetEntry
import io.quarkus.runtime.annotations.RegisterForReflection
import java.time.LocalDate
import javax.enterprise.context.ApplicationScoped
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
        fun toEntity() = TimesheetEntry(
            Project(project!!),
            Task(task!!),
            tags!!.map { Tag(it) },
            date!!,
            durationInMinutes!!.toDuration(DurationUnit.MINUTES)
        )
    }

    @RegisterForReflection
    data class CustomerDto(
        val customerId: String? = null,
        val customerName: String? = null
    ) {
        fun toEntity() = Customer(CustomerId(customerId!!), CustomerName(customerName!!))
    }
}
