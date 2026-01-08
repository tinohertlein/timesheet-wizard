package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.util.Locale
import kotlin.time.Duration

private val cellCopyPolicy = CellCopyPolicy.Builder().cellValue(false).build()

internal class XlsxV2 : ExportStrategy {

    companion object {
        private val locale: Locale = Locale.GERMAN

        private fun format(task: ExportTimesheet.Entry.Task) = task.name
        private fun format(duration: Duration): String =
            duration.toComponents { hours, minutes, _, _ -> String.format(locale, "%02d:%02d", hours, minutes) }
    }

    override fun type(): TimesheetDocument.Type {
        return TimesheetDocument.Type.XLSX_V2
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()

        template("${type()}/timesheet_template.xlsx")
            .use { template ->
                XSSFWorkbook(template).use { workbook ->
                    outputStream.use { out ->
                        val sheet = workbook.getSheetAt(0)
                        fillInEntries(sheet, timesheet.entriesGroupedByProjectAndTaskAndTagsAndStartDate())
                        autoSizeColumnWidths(sheet)
                        workbook.write(out)
                    }
                }
            }

        return TimesheetDocument(
            TimesheetDocument.Type.XLSX_V2,
            timesheet.customer.name,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun fillInEntries(sheet: XSSFSheet, entries: List<ExportTimesheet.Entry>) {
        val rowOffset = 1
        val columnOffset = 0
        val referenceRow = sheet.getRow(rowOffset)

        entries
            .sortedWith(entryComparator())
            .forEachIndexed { index, entry ->
                (if (index == 0) referenceRow else createEntryRow(sheet, index + rowOffset, referenceRow))
                    .run {
                        getCell(columnOffset + 0).setCellValue(entry.dateTimeRange.start.toLocalDate())
                        getCell(columnOffset + 1).setCellValue(format(entry.task))
                        getCell(columnOffset + 2).setCellValue(format(entry.duration))
                    }
            }
    }

    private fun createEntryRow(sheet: XSSFSheet, rowNumber: Int, referenceRow: XSSFRow): XSSFRow =
        sheet
            .createRow(rowNumber)
            .also { it.copyRowFrom(referenceRow, cellCopyPolicy) }

    private fun entryComparator(): Comparator<ExportTimesheet.Entry> =
        compareBy(
            { it.dateTimeRange.start },
            { it.task.name },
            { it.duration })

    private fun autoSizeColumnWidths(sheet: XSSFSheet) {
        for (columnIndex in 0..2) {
            sheet.autoSizeColumn(columnIndex)
        }
    }
}