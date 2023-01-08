package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody.ClientsFilter
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody.DetailedFilter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("RequestBody")
internal class RequestBodyTest {

    @Nested
    inner class WithIncrementedPageNumber {

        @Test
        fun `should increment page number`() {
            val requestBody =
                RequestBody(
                    clients = ClientsFilter(""),
                    dateRangeStart = "",
                    dateRangeEnd = "",
                    detailedFilter = DetailedFilter()
                )

            val withIncrementedPageNumber = requestBody.withIncrementedPageNumber()

            assertThat(withIncrementedPageNumber.page()).isEqualTo(2)
        }
    }
}
