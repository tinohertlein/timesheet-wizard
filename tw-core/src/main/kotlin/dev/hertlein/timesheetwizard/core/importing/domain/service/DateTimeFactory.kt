package dev.hertlein.timesheetwizard.core.importing.domain.service

import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.CUSTOM_MONTH
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.CUSTOM_YEAR
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.LAST_MONTH
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.LAST_WEEK
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.LAST_YEAR
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.THIS_MONTH
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.THIS_WEEK
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType.THIS_YEAR
import java.time.Clock
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfYear
import java.time.temporal.WeekFields

const val TIMEZONE = "Europe/Berlin"

internal class DateTimeFactory(private val clock: Clock = Clock.system(ZoneId.of(TIMEZONE))) {

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
            THIS_WEEK -> thisWeek()
            LAST_WEEK -> lastWeek()
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

    private fun customMonth(yearMonth: YearMonth): ClosedRange<LocalDate> = yearMonth.atDay(1).let {
        it..it.with(lastDayOfMonth())
    }

    private fun thisWeek(): ClosedRange<LocalDate> = customWeek(today())

    private fun lastWeek(): ClosedRange<LocalDate> = customWeek(today().minusWeeks(1))

    private fun customWeek(startDate: LocalDate): ClosedRange<LocalDate> {
        val firstDayOfMonth = YearMonth.from(startDate).atDay(1)
        val firstDayOfWeek = startDate.with(TemporalAdjusters.previousOrSame(WeekFields.ISO.firstDayOfWeek))
        val lastDayOfWeek = firstDayOfWeek.plusDays(6)

        /* If the week includes a change of the month, then the start is the first day of the month. */
        return listOf(firstDayOfWeek, firstDayOfMonth).maxOf { it }..listOf(lastDayOfWeek, today()).minOf { it }
    }
}
