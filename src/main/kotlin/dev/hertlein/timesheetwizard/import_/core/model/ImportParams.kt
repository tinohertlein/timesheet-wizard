package dev.hertlein.timesheetwizard.import_.core.model

data class ImportParams(
    val customerIds: List<String>,
    val dateRangeType: DateRangeType,
    val dateRange: String? = null
)

