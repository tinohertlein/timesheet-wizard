package dev.hertlein.timesheetwizard.generateexports.model

import java.time.LocalDate

data class TimesheetDocument(
    val type: Type,
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
) {

    enum class Type {
        EXCEL
    }
}
