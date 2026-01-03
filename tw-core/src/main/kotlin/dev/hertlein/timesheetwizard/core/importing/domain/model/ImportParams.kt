package dev.hertlein.timesheetwizard.core.importing.domain.model

data class ImportParams(
    val customerIds: List<String>,
    val dateRangeType: DateRangeType,
    val dateRange: String? = null
)

