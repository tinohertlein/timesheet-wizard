package dev.hertlein.timesheetwizard.importclockify.application.factory

import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.CUSTOM_MONTH
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.CUSTOM_YEAR
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.LAST_MONTH
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.LAST_YEAR
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.THIS_MONTH
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType.THIS_YEAR
import jakarta.inject.Singleton
import java.time.Clock
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfYear

@Singleton
class DateTimeFactory(private val clock: Clock) {

    fun create(
        dateRangeType: DateRangeType,
        dateRange: String? = null
    ): ClosedRange<LocalDate> =
        when (dateRangeType) {
            THIS_YEAR -> thisYear()
            LAST_YEAR -> lastYear()
            CUSTOM_YEAR -> customYear(Year.parse(dateRange))
            THIS_MONTH -> thisMonth()
            LAST_MONTH -> lastMonth()
            CUSTOM_MONTH -> customMonth(YearMonth.parse(dateRange))
        }

    private fun today(): LocalDate = LocalDate.now(clock)

    private fun thisYear() = customYear(Year.from(today()))

    private fun lastYear() = customYear(Year.from(today().minusYears(1)))

    private fun customYear(year: Year): ClosedRange<LocalDate> {
        val today = today()
        val firstDayOfYear = year.atDay(1)
        return if (year == Year.from(today)) {
            firstDayOfYear..today
        } else {
            firstDayOfYear..firstDayOfYear.with(lastDayOfYear())
        }
    }

    private fun thisMonth() = customMonth(YearMonth.from(today()))

    private fun lastMonth() = customMonth(YearMonth.from(today().minusMonths(1)))

    private fun customMonth(yearMonth: YearMonth): ClosedRange<LocalDate> {
        val today = today()
        val firstDayOfMonth = yearMonth.atDay(1)

        return if (yearMonth == YearMonth.from(today)) {
            firstDayOfMonth..today
        } else {
            firstDayOfMonth..firstDayOfMonth.with(lastDayOfMonth())
        }
    }
}
