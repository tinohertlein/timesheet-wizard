package dev.hertlein.timesheetwizard.core.util

import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

class ExcelSheetAssert(actual: ByteArray) : AbstractAssert<ExcelSheetAssert, ByteArray>(actual, ExcelSheetAssert::class.java) {

    override fun isEqualTo(expected: Any): ExcelSheetAssert {
        return this.hasSameNumberOfRowsAs(expected as ByteArray).hasSameCellValuesAs(expected)
    }

    fun hasSameNumberOfRowsAs(expected: ByteArray): ExcelSheetAssert {
        isNotNull

        val actualSheet = sheetFrom(actual)
        val expectedSheet = sheetFrom(expected)

        Assertions.assertThat(actualSheet.lastRowNum)
            .withFailMessage(
                """Number of rows differ: 
                    |Expected: '%d'
                    |Actual:   '%d'"""
                    .trimMargin(), expectedSheet.lastRowNum, actualSheet.lastRowNum
            )
            .isEqualTo(expectedSheet.lastRowNum)

        return this
    }

    fun hasSameCellValuesAs(expected: ByteArray): ExcelSheetAssert {
        isNotNull

        val actualSheet = sheetFrom(actual)
        val expectedSheet = sheetFrom(expected)
        val cellFormatter = DataFormatter()
        val maxRowIndex = expectedSheet.lastRowNum

        (0..maxRowIndex).forEach { rowIndex: Int ->
            (0..expectedSheet.getRow(rowIndex).lastCellNum).forEach { columnIndex: Int ->
                val expectedCell = cellValue(cellFormatter, expectedSheet, rowIndex, columnIndex)
                val actualCell = cellValue(cellFormatter, actualSheet, rowIndex, columnIndex)

                assertThat(actualCell).withFailMessage(
                    """Cells in row %d column %d differ: 
                        |Expected: '%s' 
                        |Actual:   '%s'"""
                        .trimMargin(),
                    rowIndex,
                    columnIndex,
                    expectedCell,
                    actualCell
                ).isEqualTo(expectedCell)
            }
        }
        return this
    }

    private fun sheetFrom(byteArray: ByteArray): XSSFSheet = XSSFWorkbook(byteArray.inputStream()).getSheetAt(0)

    private fun cellValue(formatter: DataFormatter, sheet: XSSFSheet, rowIndex: Int, columnIndex: Int): String =
        formatter.formatCellValue(sheet.getRow(rowIndex).getCell(columnIndex))

    companion object {

        fun assertThat(actual: ByteArray) = ExcelSheetAssert(actual)
    }
}
