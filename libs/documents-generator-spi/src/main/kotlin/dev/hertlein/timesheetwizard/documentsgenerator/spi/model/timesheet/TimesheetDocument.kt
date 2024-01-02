package dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet

import java.time.LocalDate

data class TimesheetDocument(
    val type: Type,
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
) {

    enum class Type {
        XLSX, PDF, CSV
    }
}
