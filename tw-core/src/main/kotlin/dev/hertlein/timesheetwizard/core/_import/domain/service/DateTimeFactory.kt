package dev.hertlein.timesheetwizard.core._import.domain.service

import dev.hertlein.timesheetwizard.core._import.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core._import.domain.model.DateRangeType.*
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfYear

@Component
internal class DateTimeFactory(private val clock: Clock) {

    fun create(
        dateRangeType: DateRangeType,
        dateRange: String? = null
    ): ClosedRange<LocalDate> =
        when (dateRangeType) {
            THIS_YEAR -> thisYear()
            LAST_YEAR -> lastYear()
            CUSTOM_YEAR -> customYear(Year.parse(requireNotNull(dateRange)))
            THIS_MONTH -> thisMonth()
            LAST_MONTH -> lastMonth()
            CUSTOM_MONTH -> customMonth(YearMonth.parse(requireNotNull(dateRange)))
        }

    private fun today(): LocalDate = LocalDate.now(clock)

    private fun thisYear() = customYear(Year.from(today()))

    private fun lastYear() = customYear(Year.from(today().minusYears(1)))

    private fun customYear(year: Year): ClosedRange<LocalDate> {
        val today = today()
        val firstDayOfYear = year.atDay(1)
        val currentYear = Year.from(today)

        return if (year.compareTo(currentYear) == 0) {
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
        val currentYearMonth = YearMonth.from(today)

        return if (yearMonth.compareTo(currentYearMonth) == 0) {
            firstDayOfMonth..today
        } else {
            firstDayOfMonth..firstDayOfMonth.with(lastDayOfMonth())
        }
    }
}
