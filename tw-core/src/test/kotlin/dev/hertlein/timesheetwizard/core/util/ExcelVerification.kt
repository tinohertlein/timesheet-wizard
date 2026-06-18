package dev.hertlein.timesheetwizard.core.util

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions

object ExcelVerification {

    fun assertEquals(actual: ByteArray, expected: ByteArray, maxColumnIndex: Int) {
        val actualSheet = sheetFrom(actual)
        val expectedSheet = sheetFrom(expected)
        val cellFormatter = DataFormatter()
        val maxRowIndex = expectedSheet.lastRowNum

        Assertions.assertThat(actualSheet.lastRowNum).isEqualTo(expectedSheet.lastRowNum)

        (0..maxRowIndex).forEach { rowIndex: Int ->
            (0..maxColumnIndex).forEach { columnIndex: Int ->
                val actualCell = cellValue(cellFormatter, actualSheet, rowIndex, columnIndex)
                val expectedCell = cellValue(cellFormatter, expectedSheet, rowIndex, columnIndex)

                Assertions.assertThat(actualCell).withFailMessage(
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