package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument.Type
import dev.hertlein.timesheetwizard.documentsgenerator.util.TestMother.aCustomer
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
                Type.XLSX,
                aCustomer(name = "the-customer"),
                start..end,
                ByteArray(0)
            )

            val filename = filenameFactory.create(DocumentMetaData.of(Type.XLSX), timesheetDocument)

            assertThat(filename).isEqualTo("the-customer/timesheet_20220101-20220131.xlsx")
        }
    }
}
