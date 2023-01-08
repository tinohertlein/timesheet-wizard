package dev.hertlein.timesheetwizard.generateexcel.model

import java.time.LocalDate

data class Excel(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val content: ByteArray
)
