package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report

import org.assertj.core.api.SoftAssertions.assertSoftly
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
        fun `should create request body with client filter`() {
            val dateStart = LocalDate.of(2022, 1, 1)
            val dateEnd = LocalDate.of(2022, 12, 31)
            val aClockifyId = "23571113"

            val requestBody = requestBodyFactory.requestBodyFrom(
                aClockifyId,
                dateStart..dateEnd
            )

            assertSoftly { softly ->
                softly.assertThat(requestBody.clients).isEqualTo(RequestBody.ClientsFilter(aClockifyId))
                softly.assertThat(requestBody.dateRangeStart).isEqualTo("2022-01-01T00:00:00")
                softly.assertThat(requestBody.dateRangeEnd).isEqualTo("2022-12-31T23:59:59.999999999")
                softly.assertThat(requestBody.detailedFilter.options.totals).isEqualTo("EXCLUDE")
                softly.assertThat(requestBody.detailedFilter.page).isEqualTo(1)
                softly.assertThat(requestBody.detailedFilter.pageSize).isEqualTo(1000)
            }
        }

        @Test
        fun `should create request body without client filter`() {
            val dateStart = LocalDate.of(2022, 1, 1)
            val dateEnd = LocalDate.of(2022, 12, 31)

            val requestBody = requestBodyFactory.requestBodyFrom(
                dateStart..dateEnd
            )

            assertSoftly { softly ->
                softly.assertThat(requestBody.clients).isNull()
                softly.assertThat(requestBody.dateRangeStart).isEqualTo("2022-01-01T00:00:00")
                softly.assertThat(requestBody.dateRangeEnd).isEqualTo("2022-12-31T23:59:59.999999999")
                softly.assertThat(requestBody.detailedFilter.options.totals).isEqualTo("EXCLUDE")
                softly.assertThat(requestBody.detailedFilter.page).isEqualTo(1)
                softly.assertThat(requestBody.detailedFilter.pageSize).isEqualTo(1000)
            }
        }

    }
}