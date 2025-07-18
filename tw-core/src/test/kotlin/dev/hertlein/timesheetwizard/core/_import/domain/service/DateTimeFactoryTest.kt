package dev.hertlein.timesheetwizard.core._import.domain.service

import dev.hertlein.timesheetwizard.core._import.domain.model.DateRangeType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@DisplayName("DateTimeFactory")
class DateTimeFactoryTest {
    private val clock: Clock = mockk()

    private val dateTimeFactory = DateTimeFactory(clock)

    @BeforeEach
    fun beforeEach() {
        every { clock.zone } returns ZoneId.of(TIMEZONE)
        every { clock.instant() } returns Instant.parse("2022-07-10T12:00:00.00Z")
    }

    @Nested
    inner class Create {

        @Test
        fun `should return date range for this year`() {
            dateTimeFactory.create(DateRangeType.THIS_YEAR).run {
                assertThat(start).isEqualTo(LocalDate.of(2022, 1, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2022, 7, 10))
            }
        }

        @Test
        fun `should return date range for this month`() {
            dateTimeFactory.create(DateRangeType.THIS_MONTH).run {
                assertThat(start).isEqualTo(LocalDate.of(2022, 7, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2022, 7, 10))
            }
        }

        @Test
        fun `should return date range for last month`() {
            dateTimeFactory.create(DateRangeType.LAST_MONTH).run {
                assertThat(start).isEqualTo(LocalDate.of(2022, 6, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2022, 6, 30))
            }
        }

        @Test
        fun `should return date range for last month of year before`() {
            every { clock.instant() } returns Instant.parse("2022-01-10T12:00:00.00Z")

            dateTimeFactory.create(DateRangeType.LAST_MONTH).run {
                assertThat(start).isEqualTo(LocalDate.of(2021, 12, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2021, 12, 31))
            }
        }

        @Test
        fun `should return date range for last year`() {
            dateTimeFactory.create(DateRangeType.LAST_YEAR).run {
                assertThat(start).isEqualTo(LocalDate.of(2021, 1, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2021, 12, 31))
            }
        }

        @Test
        fun `should return date range for custom year if it's this year`() {
            dateTimeFactory.create(DateRangeType.CUSTOM_YEAR, "2022").run {
                assertThat(start).isEqualTo(LocalDate.of(2022, 1, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2022, 7, 10))
            }
        }

        @Test
        fun `should return date range for custom year if it's a previous year`() {
            dateTimeFactory.create(DateRangeType.CUSTOM_YEAR, "2021").run {
                assertThat(start).isEqualTo(LocalDate.of(2021, 1, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2021, 12, 31))
            }
        }

        @Test
        fun `should return date range for custom month if it's a previous month`() {
            dateTimeFactory.create(DateRangeType.CUSTOM_MONTH, "2021-06").run {
                assertThat(start).isEqualTo(LocalDate.of(2021, 6, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2021, 6, 30))
            }
        }

        @Test
        fun `should return date range for custom month if it's this month`() {
            dateTimeFactory.create(DateRangeType.CUSTOM_MONTH, "2022-07").run {
                assertThat(start).isEqualTo(LocalDate.of(2022, 7, 1))
                assertThat(endInclusive).isEqualTo(LocalDate.of(2022, 7, 10))
            }
        }
    }
}