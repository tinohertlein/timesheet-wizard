package dev.hertlein.timesheetwizard.generateexcel.application

import dev.hertlein.timesheetwizard.generateexcel.application.config.Contact
import dev.hertlein.timesheetwizard.generateexcel.model.Excel
import dev.hertlein.timesheetwizard.generateexcel.model.Timesheet
import dev.hertlein.timesheetwizard.generateexcel.model.TimesheetEntry
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Suppress("MagicNumber")
@Singleton
class ExcelFactory(
    private val contact: Contact
) {

    fun create(timesheet: Timesheet): Excel {
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

                    workbook.write(outputStream)
                }
            }
        }

        return Excel(timesheet.customer, timesheet.dateRange, byteArrayOutputStream.toByteArray())
    }

    private fun fillInContact(sheet: XSSFSheet, contact: Contact) {
        val rowOffset = 1
        val columnOffset = 1
        sheet.getRow(rowOffset).run {
            getCell(columnOffset).setCellValue(contact.name())
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

        entries
            .sortedWith(compareBy({ it.date }, { it.project.name }))
            .forEachIndexed { index, entry ->
                sheet.getRow(index + rowOffset).run {
                    getCell(columnOffset + 0).setCellValue(entry.date)
                    getCell(columnOffset + 1).setCellValue(entry.project.name)
                    getCell(columnOffset + 2).setCellValue(entry.tags.joinToString(" ") { it.name })
                    getCell(columnOffset + 3).setCellValue(entry.task.name)
                    getCell(columnOffset + 4).setCellValue(entry.duration.toDouble(DurationUnit.HOURS))
                }
            }
    }
}
