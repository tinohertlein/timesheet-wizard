package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

internal class FilenameFactory {

    fun filenameFrom(metaData: DocumentMetaData, timesheetDocument: TimesheetDocument): String {
        val customerName = timesheetDocument.customerName
        val startDate = formatLocalDate(timesheetDocument.dateRange.start)
        val endDate = formatLocalDate(timesheetDocument.dateRange.endInclusive)

        return "timesheets/$customerName/${metaData.suffix}/${metaData.version}/timesheet_$startDate-$endDate.${metaData.suffix}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)
}
