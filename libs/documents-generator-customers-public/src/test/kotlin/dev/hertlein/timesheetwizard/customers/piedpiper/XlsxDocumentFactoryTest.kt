package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.customers.util.ResourcesReader
import dev.hertlein.timesheetwizard.customers.util.TestMother.aContact
import dev.hertlein.timesheetwizard.customers.util.TestMother.aTimesheet
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("XlsxDocumentFactory")
internal class XlsxDocumentFactoryTest {

    @Test
    fun `should create an xlsx document from timesheet`() {
        val expected = ResourcesReader.bytesFromResourceFile("timesheet_PiedPiper_20220101-20221231.xlsx")

        val actual = XlsxDocumentFactory().create(aContact(), aTimesheet())

        ExcelVerification.assertEquals(actual.content, expected)
    }

    object ExcelVerification {

        fun assertEquals(actual: ByteArray, expected: ByteArray) {
            val actualSheet = sheetFrom(actual)
            val expectedSheet = sheetFrom(expected)
            val cellFormatter = DataFormatter()
            val maxRowIndex = expectedSheet.lastRowNum
            val maxColumnIndex = 4

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
