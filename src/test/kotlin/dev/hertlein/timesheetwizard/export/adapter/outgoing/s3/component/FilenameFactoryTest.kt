package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component

import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.util.TestMother.aCustomer
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
                aCustomer().copy(name=Customer.Name("the-customer")),
                start..end,
                ByteArray(0)
            )

            val filename = filenameFactory.filenameFrom(DocumentMetaData.from(TimesheetDocument.Type.XLSX_V1), timesheetDocument)

            assertThat(filename).isEqualTo("customers/the-customer/xlsx/v1/timesheet_20220101-20220131.xlsx")
        }
    }
}
