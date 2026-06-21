package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("FilePathFactory")
internal class FilePathFactoryTest {

    private val filepathFactory = FilePathFactory()

    @Nested
    inner class FilePathFrom {

        @Test
        fun `should create the file path`() {
            val start = LocalDate.of(2022, 1, 1)
            val end = LocalDate.of(2022, 1, 31)
            val timesheetDocument = TimesheetDocument(
                ExportType.XLSX_V1,
                "timesheet_20220101-20220131.xlsx",
                "the-customer",
                start..end,
                ByteArray(0)
            )

            val filePath = filepathFactory.filePathFrom(timesheetDocument)

            assertThat(filePath).isEqualTo("timesheets/the-customer/xlsx/v1/timesheet_20220101-20220131.xlsx")
        }
    }
}
