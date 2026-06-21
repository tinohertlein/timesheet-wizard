package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val cellCopyPolicy = CellCopyPolicy.Builder().cellValue(false).build()

internal class XlsxV3(repositoryPort: RepositoryPort) : DocumentExportStrategy(repositoryPort) {

    companion object {

        private val timezone = ZoneId.of("Europe/Berlin")
        private val locale: Locale = Locale.GERMAN

        private fun format(project: ExportTimesheet.Entry.Project) = project.name
        private fun format(task: ExportTimesheet.Entry.Task) = task.name
        private fun format(tags: List<ExportTimesheet.Entry.Tag>) = tags.joinToString(" ") { it.name }
        private fun format(description: ExportTimesheet.Entry.Description) = description.value
        private fun format(billable: Boolean) = if (billable) 1.0 else 0.0
        private fun format(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", locale))
        private fun format(time: LocalTime): String = time.format(DateTimeFormatter.ofPattern("H:mm", locale))
        private fun format(time: OffsetDateTime): String = format(time.atZoneSameInstant(timezone).toLocalTime())

    }

    override fun type(): ExportType {
        return ExportType.XLSX_V3
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()

        template("${type()}/timesheet-template.xlsx")
            .use { template ->
                XSSFWorkbook(template).use { workbook ->
                    outputStream.use { out ->
                        val sheet = workbook.getSheetAt(0)
                        fillInEntries(exportParams, sheet, timesheet.entries)
                        autoSizeColumnWidths(sheet)
                        workbook.write(out)
                    }
                }
            }

        return TimesheetDocument(
            ExportType.XLSX_V3,
            timesheet.customer.name,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun fillInEntries(exportParams: Map<String, String>, sheet: XSSFSheet, entries: List<ExportTimesheet.Entry>) {
        val rowOffset = 1
        val columnOffset = 0
        val referenceRow = sheet.getRow(rowOffset)

        entries
            .sortedWith(entryComparator())
            .forEachIndexed { index, entry ->
                (if (index == 0) referenceRow else createEntryRow(sheet, index + rowOffset, referenceRow))
                    .run {
                        getCell(columnOffset + 0).setCellValue(exportParams["user"])
                        getCell(columnOffset + 1).setCellValue(format(entry.dateTimeRange.start.toLocalDate()))
                        getCell(columnOffset + 2).setCellValue(format(entry.dateTimeRange.start))
                        getCell(columnOffset + 3).setCellValue(format(entry.dateTimeRange.end))
                        getCell(columnOffset + 4).setCellValue(format(entry.project))
                        getCell(columnOffset + 5).setCellValue(format(entry.task))
                        getCell(columnOffset + 6).setCellValue(format(entry.description))
                        getCell(columnOffset + 7).setCellValue(format(entry.tags))
                        getCell(columnOffset + 8).setCellValue(format(entry.billable))
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
            { it.task.name },
            { it.duration })

    private fun autoSizeColumnWidths(sheet: XSSFSheet) {
        for (columnIndex in 0..8) {
            sheet.autoSizeColumn(columnIndex)
        }
    }
}