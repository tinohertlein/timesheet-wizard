package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import dev.hertlein.timesheetwizard.importer.application.factory.UUIDFactory
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Singleton
class FilenameFactory(
    private val uuidFactory: UUIDFactory
) {

    fun create(timesheet: Timesheet): String {
        fun formatLocalDate(localDate: LocalDate): String = DateTimeFormatter.ISO_DATE.format(localDate)

        val customerName = timesheet.customer.customerName.value
        val startDate = formatLocalDate(timesheet.dateRange.start)
        val endDate = formatLocalDate(timesheet.dateRange.endInclusive)
        val uuid = uuidFactory.create()
        val suffix = "json"

        return "$customerName/${startDate}_${endDate}_${uuid}.${suffix}"
    }
}
