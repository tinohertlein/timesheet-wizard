package dev.hertlein.timesheetwizard.core.exporting.domain.model

import java.time.LocalDate

internal data class TimesheetDocument(
    val exportType: ExportType,
    val fileName: String,
    val customerName: String,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
)
