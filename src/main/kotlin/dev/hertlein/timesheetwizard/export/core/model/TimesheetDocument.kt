package dev.hertlein.timesheetwizard.export.core.model

import dev.hertlein.timesheetwizard.shared.model.Customer
import java.time.LocalDate

data class TimesheetDocument(
    val type: Type,
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
) {

    enum class Type {
        XLSX_V1, XLSX_V2, PDF_V1, CSV_V1
    }
}
