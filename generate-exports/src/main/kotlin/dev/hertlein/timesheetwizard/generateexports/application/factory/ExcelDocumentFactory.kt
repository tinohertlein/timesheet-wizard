package dev.hertlein.timesheetwizard.generateexports.application.factory

import dev.hertlein.timesheetwizard.generateexports.application.config.Contact
import dev.hertlein.timesheetwizard.generateexports.model.Project
import dev.hertlein.timesheetwizard.generateexports.model.Tag
import dev.hertlein.timesheetwizard.generateexports.model.Task
import dev.hertlein.timesheetwizard.generateexports.model.Timesheet
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetEntry
import jakarta.inject.Singleton
import org.apache.poi.ss.usermodel.CellCopyPolicy
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit


private val cellCopyPolicy = CellCopyPolicy.Builder().cellValue(false).build()

private fun Project.format() = this.name
private fun Task.format() = this.name
private fun List<Tag>.format() = this.joinToString(" ") { it.name }

@Suppress("MagicNumber")
@Singleton
class ExcelDocumentFactory(
    private val contact: Contact
) : TimesheetDocumentFactory {

    override fun apply(timesheet: Timesheet): TimesheetDocument {
        val template = Thread.currentThread().contextClassLoader.getResourceAsStream("timesheet_template.xlsx")

        val byteArrayOutputStream = ByteArrayOutputStream()

        template.use { inputStream ->
            XSSFWorkbook(inputStream).use { workbook ->
                byteArrayOutputStream.use { outputStream ->
                    val sheet = workbook.getSheetAt(0)
                    fillInContact(sheet, contact)
                    fillInDateRange(sheet, timesheet.dateRange)
                    fillInTotalWorkedHours(sheet, timesheet.totalDuration())
                    fillInEntries(sheet, timesheet.entries)
                    autoSizeColumnWidths(sheet)
                    workbook.write(outputStream)
                }
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.EXCEL,
            timesheet.customer,
            timesheet.dateRange,
            byteArrayOutputStream.toByteArray()
        )
    }

    private fun fillInContact(sheet: XSSFSheet, contact: Contact) {
        val rowOffset = 1
        val columnOffset = 1
        sheet.getRow(rowOffset).run {
            getCell(columnOffset).setCellValue(contact.name().replace('_', ' '))
        }
        sheet.getRow(rowOffset + 1).run {
            getCell(columnOffset).setCellValue(contact.email())
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

    private fun fillInEntries(sheet: XSSFSheet, entries: List<TimesheetEntry>) {
        val rowOffset = 6
        val columnOffset = 0
        val referenceRow = sheet.getRow(rowOffset)

        entries
            .sortedWith(entryComparator())
            .forEachIndexed { index, entry ->
                (if (index == 0) referenceRow else createEntryRow(sheet, index + rowOffset, referenceRow))
                    .run {
                        getCell(columnOffset + 0).setCellValue(entry.date)
                        getCell(columnOffset + 1).setCellValue(entry.project.format())
                        getCell(columnOffset + 2).setCellValue(entry.tags.format())
                        getCell(columnOffset + 3).setCellValue(entry.task.format())
                        getCell(columnOffset + 4).setCellValue(entry.duration.toDouble(DurationUnit.HOURS))
                    }
            }
    }

    private fun createEntryRow(
        sheet: XSSFSheet,
        rowNumber: Int,
        referenceRow: XSSFRow
    ): XSSFRow =
        sheet
            .createRow(rowNumber)
            .also { it.copyRowFrom(referenceRow, cellCopyPolicy) }

    private fun entryComparator(): Comparator<TimesheetEntry> =
        compareBy(
            { it.date },
            { it.project.name },
            { it.tags.format() },
            { it.duration })

    private fun autoSizeColumnWidths(sheet: XSSFSheet) {
        for (columnIndex in 0..4) {
            sheet.autoSizeColumn(columnIndex)
        }
    }
}
