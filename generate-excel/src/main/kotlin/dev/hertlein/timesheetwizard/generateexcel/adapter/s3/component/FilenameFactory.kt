package dev.hertlein.timesheetwizard.generateexcel.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexcel.model.Excel
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale
import jakarta.inject.Singleton

@Singleton
class FilenameFactory {

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    private fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)

    fun create(prefix: String, excel: Excel): String {

        val customerName = excel.customer.customerName.value
        val startDate = formatLocalDate(excel.dateRange.start)
        val endDate = formatLocalDate(excel.dateRange.endInclusive)
        val suffix = "xlsx"

        return "$prefix/timesheet_${customerName}_${startDate}-${endDate}.${suffix}"
    }
}
