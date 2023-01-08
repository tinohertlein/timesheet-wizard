package dev.hertlein.timesheetwizard.importclockify.adapter.s3.component

import dev.hertlein.timesheetwizard.importclockify.application.factory.UUIDFactory
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Singleton
class FilenameFactory(
    private val uuidFactory: UUIDFactory
) {

    fun create(prefix: String, timesheet: Timesheet): String {
        fun formatLocalDate(localDate: LocalDate): String = DateTimeFormatter.ISO_DATE.format(localDate)

        val customerName = timesheet.customer.customerName.value
        val startDate = formatLocalDate(timesheet.dateRange.start)
        val endDate = formatLocalDate(timesheet.dateRange.endInclusive)
        val uuid = uuidFactory.create()
        val suffix = "json"

        return "$prefix/${customerName}_${startDate}_${endDate}_${uuid}.${suffix}"
    }
}
