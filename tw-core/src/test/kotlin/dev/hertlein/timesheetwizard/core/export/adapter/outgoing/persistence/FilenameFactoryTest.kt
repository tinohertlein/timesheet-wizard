package dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence

import dev.hertlein.timesheetwizard.core.export.domain.model.TimesheetDocument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("FilenameFactory")
internal class FilenameFactoryTest {

    private val filenameFactory = FilenameFactory()

    @Nested
    inner class FilenameFrom {

        @Test
        fun `should create the filename`() {
            val start = LocalDate.of(2022, 1, 1)
            val end = LocalDate.of(2022, 1, 31)
            val timesheetDocument = TimesheetDocument(
                TimesheetDocument.Type.XLSX_V1,
                "the-customer",
                start..end,
                ByteArray(0)
            )

            val filename =
                filenameFactory.filenameFrom(DocumentMetaData.from(TimesheetDocument.Type.XLSX_V1), timesheetDocument)

            assertThat(filename).isEqualTo("customers/the-customer/xlsx/v1/timesheet_20220101-20220131.xlsx")
        }
    }
}
