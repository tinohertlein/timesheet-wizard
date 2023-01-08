package dev.hertlein.timesheetwizard.importclockify.adapter.s3.component

import dev.hertlein.timesheetwizard.importclockify.application.factory.UUIDFactory
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import dev.hertlein.timesheetwizard.importclockify.util.TestMother
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

@DisplayName("FilenameFactory")
internal class FilenameFactoryTest {

    private val uuidFactory: UUIDFactory = mockk()

    private val filenameFactory = FilenameFactory(uuidFactory)

    @Nested
    inner class Create {

        @Test
        fun `should create the filename`() {
            every { uuidFactory.create() } returns UUID(0, 1)
            val start = LocalDate.of(2022, 1, 1)
            val end = LocalDate.of(2022, 1, 31)
            val timesheet = Timesheet(TestMother.aCustomer(name = "the-customer"), start..end)

            val filename = filenameFactory.create("JSON", timesheet)

            assertThat(filename).isEqualTo(
                "JSON/" +
                        "the-customer_2022-01-01_2022-01-31_00000000-0000-0000-0000-000000000001.json"
            )

        }
    }
}
