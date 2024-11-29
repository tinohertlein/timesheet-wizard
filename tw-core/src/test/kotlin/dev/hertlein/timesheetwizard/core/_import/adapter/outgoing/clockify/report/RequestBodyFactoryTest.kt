package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("RequestBodyFactory")
internal class RequestBodyFactoryTest {

    @Nested
    inner class RequestBodyFrom {

        private val requestBodyFactory = RequestBodyFactory()

        @Test
        fun `should create request body`() {
            val dateStart = LocalDate.of(2022, 1, 1)
            val dateEnd = LocalDate.of(2022, 12, 31)
            val aClockifyId = "23571113"

            val requestBody = requestBodyFactory.requestBodyFrom(
                aClockifyId,
                dateStart..dateEnd
            )

            SoftAssertions().apply {
                assertThat(requestBody.clients).isEqualTo(RequestBody.ClientsFilter(aClockifyId))
                assertThat(requestBody.dateRangeStart).isEqualTo("2022-01-01T00:00:00")
                assertThat(requestBody.dateRangeEnd).isEqualTo("2022-12-31T23:59:59.999999999")
                assertThat(requestBody.detailedFilter.options.totals).isEqualTo("EXCLUDE")
                assertThat(requestBody.detailedFilter.page).isEqualTo(1)
                assertThat(requestBody.detailedFilter.pageSize).isEqualTo(1000)
            }.assertAll()
        }
    }
}