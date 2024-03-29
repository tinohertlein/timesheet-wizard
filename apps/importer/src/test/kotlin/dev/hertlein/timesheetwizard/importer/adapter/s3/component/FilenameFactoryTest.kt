package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import dev.hertlein.timesheetwizard.importer.application.factory.UUIDFactory
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import dev.hertlein.timesheetwizard.importer.util.TestMother
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

            val filename = filenameFactory.create(timesheet)

            assertThat(filename).isEqualTo(
                "the-customer/" +
                        "timesheet_20220101-20220131_00000000-0000-0000-0000-000000000001.json"
            )

        }
    }
}
