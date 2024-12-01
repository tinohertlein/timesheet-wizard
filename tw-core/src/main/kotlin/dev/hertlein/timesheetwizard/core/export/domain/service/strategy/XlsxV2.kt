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
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val cellCopyPolicy = CellCopyPolicy.Builder().cellValue(false).build()

@Component
internal class XlsxV2 : ExportStrategy {

    companion object {
        private val locale: Locale = Locale.GERMAN

        private fun format(task: ExportTimesheet.Entry.Task) = task.name
        private fun format(duration: Duration): String =
            duration.toComponents { hours, minutes, _, _ -> String.format(locale, "%02d:%02d", hours, minutes) }

    }

   internal data  class XlsxEntryKey(
        val project: ExportTimesheet.Entry.Project,
        val description: ExportTimesheet.Entry.Task,
        val date: LocalDate
    ) {
        companion object {
            fun of(
                project: ExportTimesheet.Entry.Project,
                task: ExportTimesheet.Entry.Task,
                startTime: OffsetDateTime
            ) = XlsxEntryKey(
                project,
                task,
                startTime.toLocalDate()
            )
        }
    }

   internal data  class XlsxEntryValue(
        val workDuration: Duration
    )

   internal data  class XlsxEntry(
       val key: XlsxEntryKey,
       val value: XlsxEntryValue
    )

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
                        fillInEntries(sheet, aggregate(timesheet.entries))
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

    private fun aggregate(entries: List<ExportTimesheet.Entry>): List<XlsxEntry> {
        return entries
            .groupBy { XlsxEntryKey.of(it.project, it.task, it.dateTimeRange.start) }
            .map { groupedEntry ->
                val workDuration = groupedEntry.value.sumOf { it.duration.inWholeMinutes }
                val value = XlsxEntryValue(workDuration.minutes)
                XlsxEntry(groupedEntry.key, value)
            }
            .sortedWith(entryComparator())
    }

    private fun fillInEntries(sheet: XSSFSheet, entries: List<XlsxEntry>) {
        val rowOffset = 1
        val columnOffset = 0
        val referenceRow = sheet.getRow(rowOffset)

        entries
            .sortedWith(entryComparator())
            .forEachIndexed { index, entry ->
                (if (index == 0) referenceRow else createEntryRow(sheet, index + rowOffset, referenceRow))
                    .run {
                        getCell(columnOffset + 0).setCellValue(entry.key.date)
                        getCell(columnOffset + 1).setCellValue(format(entry.key.description))
                        getCell(columnOffset + 2).setCellValue(format(entry.value.workDuration))
                    }
            }
    }

    private fun createEntryRow(sheet: XSSFSheet, rowNumber: Int, referenceRow: XSSFRow): XSSFRow =
        sheet
            .createRow(rowNumber)
            .also { it.copyRowFrom(referenceRow, cellCopyPolicy) }

    private fun entryComparator(): Comparator<XlsxEntry> =
        compareBy(
            { it.key.date },
            { it.key.project.name },
            { it.key.description.name },
            { it.value.workDuration })

    private fun autoSizeColumnWidths(sheet: XSSFSheet) {
        for (columnIndex in 0..2) {
            sheet.autoSizeColumn(columnIndex)
        }
    }
}