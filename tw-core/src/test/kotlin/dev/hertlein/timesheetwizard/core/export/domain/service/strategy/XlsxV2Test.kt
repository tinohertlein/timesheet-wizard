package dev.hertlein.timesheetwizard.core.export.domain.service.strategy

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aZoneOffset
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.anEntry
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aTimesheet
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("XlsxV2")
class XlsxV2Test {

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
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/timesheet_v2_PiedPiper_20220101-20221231.xlsx")

        val actual = XlsxV2().create(emptyMap(), timesheet)

        ExcelVerification.assertEquals(
            actual.content,
            expected
        )
    }

    object ExcelVerification {

        fun assertEquals(actual: ByteArray, expected: ByteArray) {
            val actualSheet = sheetFrom(actual)
            val expectedSheet = sheetFrom(expected)
            val cellFormatter = DataFormatter()
            val maxRowIndex = expectedSheet.lastRowNum
            val maxColumnIndex = 3

            assertThat(actualSheet.lastRowNum).isEqualTo(expectedSheet.lastRowNum)

            (0..maxRowIndex).forEach { rowIndex: Int ->
                (0..maxColumnIndex).forEach { columnIndex: Int ->
                    val actualCell = cellValue(cellFormatter, actualSheet, rowIndex, columnIndex)
                    val expectedCell = cellValue(cellFormatter, expectedSheet, rowIndex, columnIndex)

                    assertThat(actualCell).withFailMessage(
                        """Cells in
                            |row %d, column %d (starting both at 0)
                            |do not match: %s (actual) <-> %s (expected).""".trimMargin(),
                        rowIndex,
                        columnIndex,
                        actualCell,
                        expectedCell
                    ).isEqualTo(expectedCell)
                }
            }
        }

        private fun sheetFrom(byteArray: ByteArray): XSSFSheet = XSSFWorkbook(byteArray.inputStream()).getSheetAt(0)

        private fun cellValue(formatter: DataFormatter, sheet: XSSFSheet, rowIndex: Int, columnIndex: Int): String =
            formatter.formatCellValue(sheet.getRow(rowIndex).getCell(columnIndex))
    }

}