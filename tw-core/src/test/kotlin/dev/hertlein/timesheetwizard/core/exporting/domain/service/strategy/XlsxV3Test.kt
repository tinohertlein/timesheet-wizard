package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import dev.hertlein.timesheetwizard.core.util.ExcelSheetAssert
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aZoneOffset
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.anEntry
import io.mockk.mockk
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("XlsxV3")
class XlsxV3Test {

    @Test
    fun `should create an xlsx document from timesheet`() {
        val workDurationHours = 2L

        val workStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)
        val workEnd = workStart.plusHours(workDurationHours)
        val work = anEntry.copy(
            duration = workDurationHours.toDuration(DurationUnit.HOURS),
            dateTimeRange = ExportTimesheet.Entry.DateTimeRange(workStart, workEnd)
        )
        val timesheet = aTimesheet(work)
        val expected =
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/timesheet_v3_PiedPiper_20220101-20221231.xlsx")

        val actual =
            XlsxV3(mockk(relaxed = true)).create(mapOf("user" to "a-user"), timesheet)

        assertSoftly { softly ->
            softly.assertThat(actual.exportType).isEqualTo(ExportType.XLSX_V3)
            softly.assertThat(actual.customerName).isEqualTo(timesheet.customer.name)
            softly.assertThat(actual.dateRange).isEqualTo(timesheet.dateRange)
        }
        val _ = ExcelSheetAssert.assertThat(actual.content).isEqualTo(expected)
    }
}