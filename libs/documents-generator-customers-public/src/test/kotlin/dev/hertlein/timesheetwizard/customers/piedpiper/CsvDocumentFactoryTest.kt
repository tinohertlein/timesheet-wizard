package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.customers.util.ResourcesReader
import dev.hertlein.timesheetwizard.customers.util.TestMother.aContact
import dev.hertlein.timesheetwizard.customers.util.TestMother.aTimesheet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("CsvDocumentFactory")
internal class CsvDocumentFactoryTest {

    @Test
    fun `should create a csv document from timesheet`() {
        val expected = ResourcesReader.bytesFromResourceFile("timesheet_PiedPiper_20220101-20221231.csv")

        val actual = CsvDocumentFactory().create(aContact(), aTimesheet())

        assertThat(String(actual.content)).isEqualTo(String(expected))
    }
}
