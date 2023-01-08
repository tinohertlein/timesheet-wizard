package dev.hertlein.timesheetwizard.importclockify.application.config

import io.micronaut.core.annotation.Introspected


@Introspected
data class ImportConfig(
    val customerIds: List<String>,
    val dateRangeType: DateRangeType,
    val dateRange: String? = null
)

enum class DateRangeType {
    THIS_YEAR, LAST_YEAR, CUSTOM_YEAR, THIS_MONTH, LAST_MONTH, CUSTOM_MONTH
}
