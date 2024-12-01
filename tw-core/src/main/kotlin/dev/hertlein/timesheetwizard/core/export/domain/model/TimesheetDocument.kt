package dev.hertlein.timesheetwizard.core.export.domain.model

import java.time.LocalDate

internal data class TimesheetDocument(
    val type: Type,
    val customerName: String,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
) {

    internal enum class Type {
        XLSX_V1, XLSX_V2, PDF_V1, CSV_V1
    }
}
