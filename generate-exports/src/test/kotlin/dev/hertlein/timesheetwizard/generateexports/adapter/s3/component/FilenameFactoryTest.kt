package dev.hertlein.timesheetwizard.generateexports.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument
import dev.hertlein.timesheetwizard.generateexports.util.TestMother.aCustomer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("FilenameFactory")
internal class FilenameFactoryTest {

    private val filenameFactory = FilenameFactory()

    @Nested
    inner class Create {

        @Test
        fun `should create the filename`() {
            val start = LocalDate.of(2022, 1, 1)
            val end = LocalDate.of(2022, 1, 31)
            val timesheetDocument = TimesheetDocument(
                TimesheetDocument.Type.EXCEL,
                aCustomer(name = "the-customer"),
                start..end,
                ByteArray(0)
            )

            val filename = filenameFactory.create(DocumentMetaData.EXCEL, timesheetDocument)

            assertThat(filename).isEqualTo("xlsx/timesheet_the-customer_20220101-20220131.xlsx")
        }
    }
}
