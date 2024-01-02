package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

@Singleton
class FilenameFactory {

    fun create(metaData: DocumentMetaData, timesheetDocument: TimesheetDocument): String {
        val customerName = timesheetDocument.customer.name.value
        val startDate = formatLocalDate(timesheetDocument.dateRange.start)
        val endDate = formatLocalDate(timesheetDocument.dateRange.endInclusive)

        return "${customerName}/timesheet_${startDate}-${endDate}.${metaData.suffix}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)
}
