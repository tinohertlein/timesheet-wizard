package dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence.component

import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.*

@Component
internal class FilenameFactory {

    fun filenameFrom(metaData: DocumentMetaData, timesheetDocument: TimesheetDocument): String {
        val customerName = timesheetDocument.customer.name.value
        val startDate = formatLocalDate(timesheetDocument.dateRange.start)
        val endDate = formatLocalDate(timesheetDocument.dateRange.endInclusive)

        return "customers/$customerName/${metaData.suffix}/${metaData.version}/timesheet_$startDate-$endDate.${metaData.suffix}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)
}
