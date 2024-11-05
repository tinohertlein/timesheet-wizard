package dev.hertlein.timesheetwizard.core._import.core.model

data class ImportParams(
    val customerIds: List<String>,
    val dateRangeType: DateRangeType,
    val dateRange: String? = null
)

