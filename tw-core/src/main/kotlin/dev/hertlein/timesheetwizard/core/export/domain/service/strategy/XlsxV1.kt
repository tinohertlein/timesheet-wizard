package dev.hertlein.timesheetwizard.core.export.domain.service.strategy

import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.model.TimesheetDocument
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit

private val cellCopyPolicy = CellCopyPolicy.Builder().cellValue(false).build()

@Component
internal class XlsxV1 : ExportStrategy {

    companion object {

        private fun format(project: ExportTimesheet.Entry.Project) = project.name
        private fun format(task: ExportTimesheet.Entry.Task) = task.name
        private fun format(tags: List<ExportTimesheet.Entry.Tag>) = tags.joinToString(" ") { it.name }
    }

    override fun type(): TimesheetDocument.Type {
        return TimesheetDocument.Type.XLSX_V1
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()

        template("${type()}/timesheet_template.xlsx")
            .use { template ->
                XSSFWorkbook(template).use { workbook ->
                    outputStream.use { out ->
                        val sheet = workbook.getSheetAt(0)
                        fillInContact(exportParams, sheet)
                        fillInDateRange(sheet, timesheet.dateRange)
                        fillInTotalWorkedHours(sheet, timesheet.totalDuration())
                        fillInEntries(sheet, timesheet.entries)
                        autoSizeColumnWidths(sheet)
                        workbook.write(out)
                    }
                }
            }

        return TimesheetDocument(
            TimesheetDocument.Type.XLSX_V1,
            timesheet.customer.name,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun fillInContact(exportParams: Map<String, String>, sheet: XSSFSheet) {
        val rowOffset = 1
        val columnOffset = 1
        sheet.getRow(rowOffset).run {
            getCell(columnOffset).setCellValue(
                exportParams["contact-name"]?.replace('_', ' ')
            )
        }
        sheet.getRow(rowOffset + 1).run {
            getCell(columnOffset).setCellValue(exportParams["contact-email"])
        }
    }

    private fun fillInDateRange(sheet: XSSFSheet, dateRange: ClosedRange<LocalDate>) {
        val rowOffset = 3
        val columnOffset = 1
        sheet.getRow(rowOffset).run {
            getCell(columnOffset + 0).setCellValue(dateRange.start)
            getCell(columnOffset + 1).setCellValue(dateRange.endInclusive)
        }
    }

    private fun fillInTotalWorkedHours(sheet: XSSFSheet, totalDuration: Duration) {
        val rowOffset = 4
        val columnOffset = 1
        sheet.getRow(rowOffset).getCell(columnOffset).setCellValue(totalDuration.toDouble(DurationUnit.HOURS))
    }

    private fun fillInEntries(sheet: XSSFSheet, entries: List<ExportTimesheet.Entry>) {
        val rowOffset = 6
        val columnOffset = 0
        val referenceRow = sheet.getRow(rowOffset)

        entries
            .sortedWith(entryComparator())
            .forEachIndexed { index, entry ->
                (if (index == 0) referenceRow else createEntryRow(sheet, index + rowOffset, referenceRow))
                    .run {
                        getCell(columnOffset + 0).setCellValue(entry.dateTimeRange.start.toLocalDate())
                        getCell(columnOffset + 1).setCellValue(format(entry.project))
                        getCell(columnOffset + 2).setCellValue(format(entry.tags))
                        getCell(columnOffset + 3).setCellValue(format(entry.task))
                        getCell(columnOffset + 4).setCellValue(entry.duration.toDouble(DurationUnit.HOURS))
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
            { it.project.name },
            { format(it.tags) },
            { it.duration })

    private fun autoSizeColumnWidths(sheet: XSSFSheet) {
        for (columnIndex in 0..4) {
            sheet.autoSizeColumn(columnIndex)
        }
    }
}