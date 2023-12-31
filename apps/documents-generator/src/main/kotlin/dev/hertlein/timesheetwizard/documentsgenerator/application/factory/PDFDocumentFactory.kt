package dev.hertlein.timesheetwizard.documentsgenerator.application.factory

import dev.hertlein.timesheetwizard.documentsgenerator.application.config.Contact
import dev.hertlein.timesheetwizard.documentsgenerator.model.Project
import dev.hertlein.timesheetwizard.documentsgenerator.model.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.model.Task
import dev.hertlein.timesheetwizard.documentsgenerator.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetDocument
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetEntry
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
        val values = createValuesForLayoutParams(timesheet)
        val outputStream = ByteArrayOutputStream()

        template("timesheet_template.jrxml").use { template ->
            outputStream.use { out ->
                val report = JasperCompileManager.compileReport(template)
                val print = JasperFillManager.fillReport(report, values, JREmptyDataSource())
                JasperExportManager.exportReportToPdfStream(print, out)
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.PDF,
            timesheet.customer,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun createValuesForLayoutParams(timesheet: Timesheet) = mapOf(
        "name" to contact.name(),
        "email" to contact.email(),
        "period_start" to timesheet.dateRange.start.format(),
        "period_end" to timesheet.dateRange.endInclusive.format(),
        "total_working_hours" to timesheet.totalDuration().toDouble(DurationUnit.HOURS).format(),
        "timesheet_entries" to JRBeanCollectionDataSource(toDataSource(timesheet))
    )

    private fun toDataSource(timesheet: Timesheet) =
        timesheet.entries.sortedWith(entryComparator()).map { PDFTimesheetEntry.of(it) }

    private fun entryComparator(): Comparator<TimesheetEntry> =
        compareBy({ it.date }, { it.project.name }, { it.tags.format() }, { it.duration })
}
