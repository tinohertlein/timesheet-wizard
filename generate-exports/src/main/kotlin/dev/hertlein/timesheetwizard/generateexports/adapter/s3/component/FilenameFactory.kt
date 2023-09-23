package dev.hertlein.timesheetwizard.generateexports.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

@Singleton
class FilenameFactory {

    fun create(metaData: DocumentMetaData, timesheetDocument: TimesheetDocument): String {
        val customerName = timesheetDocument.customer.customerName.value
        val startDate = formatLocalDate(timesheetDocument.dateRange.start)
        val endDate = formatLocalDate(timesheetDocument.dateRange.endInclusive)

        return "${metaData.prefix}/timesheet_${customerName}_${startDate}-${endDate}.${metaData.suffix}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)
}
