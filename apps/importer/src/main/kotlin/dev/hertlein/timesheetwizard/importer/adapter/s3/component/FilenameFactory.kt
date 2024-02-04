package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import dev.hertlein.timesheetwizard.importer.application.factory.UUIDFactory
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

@Singleton
class FilenameFactory(
    private val uuidFactory: UUIDFactory
) {

    fun create(timesheet: Timesheet): String {
        val customerName = timesheet.customer.customerName.value
        val startDate = formatLocalDate(timesheet.dateRange.start)
        val endDate = formatLocalDate(timesheet.dateRange.endInclusive)
        val uuid = uuidFactory.create()
        val suffix = "json"

        return "$customerName/timesheet_${startDate}-${endDate}_${uuid}.${suffix}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)
}
