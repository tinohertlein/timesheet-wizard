package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.customers.util.TestMother.aPdfTimesheetEntry
import dev.hertlein.timesheetwizard.customers.util.TestMother.aTimesheetEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("PdfDocumentFactory")
internal class PdfDocumentFactoryTest {

    @Test
    fun `should create a PdfTimesheetEntry`() {
        val timesheetEntry = aTimesheetEntry()

        val pdfTimesheetEntry = PdfDocumentFactory.PdfTimesheetEntry.of(timesheetEntry)

        assertThat(pdfTimesheetEntry).isEqualTo(aPdfTimesheetEntry())
    }
}
