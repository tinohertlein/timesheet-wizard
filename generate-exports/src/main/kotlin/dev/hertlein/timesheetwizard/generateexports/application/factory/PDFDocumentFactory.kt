package dev.hertlein.timesheetwizard.generateexports.application.factory

import dev.hertlein.timesheetwizard.generateexports.application.config.Contact
import dev.hertlein.timesheetwizard.generateexports.model.Project
import dev.hertlein.timesheetwizard.generateexports.model.Tag
import dev.hertlein.timesheetwizard.generateexports.model.Task
import dev.hertlein.timesheetwizard.generateexports.model.Timesheet
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetEntry
import jakarta.inject.Singleton
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.DurationUnit

private fun Project.format() = this.name
private fun Task.format() = this.name
private fun List<Tag>.format() = this.joinToString(" ") { it.name }
private fun LocalDate.format() = this.format(DateTimeFormatter.ISO_DATE)
private fun Double.format() = DecimalFormat("0.00", DecimalFormatSymbols(Locale.GERMANY)).format(this)

@Singleton
class PDFDocumentFactory(
    private val contact: Contact
) : TimesheetDocumentFactory {

    @Suppress("ConstructorParameterNaming")
    data class PDFTimesheetEntry(
        val date: String,
        val project: String,
        val location: String,
        val activity: String,
        val working_hours: String
    ) {

        companion object {
            fun of(entry: TimesheetEntry): PDFTimesheetEntry {
                return PDFTimesheetEntry(
                    entry.date.format(),
                    entry.project.format(),
                    entry.tags.format(),
                    entry.task.format(),
                    entry.duration.toDouble(DurationUnit.HOURS).format()
                )
            }
        }
    }

    override fun apply(timesheet: Timesheet): TimesheetDocument {
        val params = mapOf(
            "name" to contact.name(),
            "email" to contact.email(),
            "period_start" to timesheet.dateRange.start.format(),
            "period_end" to timesheet.dateRange.endInclusive.format(),
            "total_working_hours" to timesheet.totalDuration().toDouble(DurationUnit.HOURS).format(),
            "timesheet_entries" to JRBeanCollectionDataSource(toDataSource(timesheet))
        )

        val template = Thread.currentThread().contextClassLoader.getResourceAsStream("timesheet_template.jrxml")
        val outputStream = ByteArrayOutputStream()

        template.use {
            outputStream.use {
                val report = JasperCompileManager.compileReport(template)
                val print = JasperFillManager.fillReport(report, params, JREmptyDataSource())
                JasperExportManager.exportReportToPdfStream(print, outputStream)
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.PDF,
            timesheet.customer,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun toDataSource(timesheet: Timesheet) =
        timesheet.entries.sortedWith(entryComparator()).map { PDFTimesheetEntry.of(it) }

    private fun entryComparator(): Comparator<TimesheetEntry> =
        compareBy({ it.date }, { it.project.name }, { it.tags.format() }, { it.duration })
}
