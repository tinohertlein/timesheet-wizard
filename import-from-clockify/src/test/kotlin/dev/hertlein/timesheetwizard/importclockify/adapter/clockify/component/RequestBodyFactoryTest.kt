package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.importclockify.util.TestMother
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("RequestBodyFactory")
internal class RequestBodyFactoryTest {

    @Nested
    inner class Create {

        private val requestBodyFactory = RequestBodyFactory()

        @Test
        fun `should create instance`() {
            val dateStart = LocalDate.of(2022, 1, 1)
            val dateEnd = LocalDate.of(2022, 12, 31)
            val aClockifyId = "23571113"

            val requestBody = requestBodyFactory.create(
                TestMother.aCustomer(clockifyId = aClockifyId),
                dateStart..dateEnd
            )

            requestBody.run {
                assertThat(clients).isEqualTo(RequestBody.ClientsFilter(aClockifyId))
                assertThat(dateRangeStart).isEqualTo("2022-01-01T00:00:00")
                assertThat(dateRangeEnd).isEqualTo("2022-12-31T23:59:59.999999999")
                assertThat(detailedFilter.options.totals).isEqualTo("EXCLUDE")
                assertThat(detailedFilter.page).isEqualTo(1)
                assertThat(detailedFilter.pageSize).isEqualTo(1000)
            }
        }
    }
}
