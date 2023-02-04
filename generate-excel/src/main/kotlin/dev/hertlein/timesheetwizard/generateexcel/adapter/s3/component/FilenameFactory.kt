package dev.hertlein.timesheetwizard.generateexcel.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexcel.model.Excel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Singleton
class FilenameFactory {

    fun create(prefix: String, excel: Excel): String {
        fun formatLocalDate(localDate: LocalDate): String = DateTimeFormatter.ISO_DATE.format(localDate)

        val customerName = excel.customer.customerName.value
        val startDate = formatLocalDate(excel.dateRange.start)
        val endDate = formatLocalDate(excel.dateRange.endInclusive)
        val suffix = "xlsx"

        return "$prefix/timesheet_${customerName}_${startDate}_${endDate}.${suffix}"
    }
}
