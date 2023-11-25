package dev.hertlein.timesheetwizard.documentsgenerator.application.factory

import dev.hertlein.timesheetwizard.documentsgenerator.application.factory.PDFDocumentFactory.PDFTimesheetEntry
import dev.hertlein.timesheetwizard.documentsgenerator.util.TestMother
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("PDFDocumentFactory")
internal class PDFDocumentFactoryTest {

    @Test
    fun `should create PDFTimesheetEntry`() {
        val timesheetEntry = TestMother.aTimesheetEntry()

        val pdfTimesheetEntry = PDFTimesheetEntry.of(timesheetEntry)

        assertThat(pdfTimesheetEntry).isEqualTo(TestMother.aPDFTimesheetEntry())
    }
}
