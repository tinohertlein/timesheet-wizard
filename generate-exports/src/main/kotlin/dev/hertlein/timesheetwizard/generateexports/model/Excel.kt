package dev.hertlein.timesheetwizard.generateexports.model

import java.time.LocalDate

data class Excel(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
)
