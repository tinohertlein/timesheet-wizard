package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.Locale
import kotlin.time.DurationUnit

internal class PdfV1 : ExportStrategy {

    companion object {

        private fun format(project: ExportTimesheet.Entry.Project) = project.name
        private fun format(task: ExportTimesheet.Entry.Task) = task.name
        private fun format(tags: List<ExportTimesheet.Entry.Tag>) = tags.joinToString(" ") { it.name }
        private fun format(date: LocalDate): String = date.format(java.time.format.DateTimeFormatter.ISO_DATE)
        private fun format(dateTime: OffsetDateTime): String = format(dateTime.toLocalDate())
        private fun format(double: Double): String =
            DecimalFormat("0.00", DecimalFormatSymbols(Locale.GERMANY)).format(double)
    }

    @Suppress("ConstructorParameterNaming")
    internal data class PdfTimesheetEntry(
        val date: String, val project: String, val location: String, val activity: String, val working_hours: String
    ) {

        companion object {
            fun of(entry: ExportTimesheet.Entry): PdfTimesheetEntry {
                return PdfTimesheetEntry(
                    format(entry.dateTimeRange.start),
                    format(entry.project),
                    format(entry.tags),
                    format(entry.task),
                    format(entry.duration.toDouble(DurationUnit.HOURS))
                )
            }
        }
    }

    init {
        DefaultJasperReportsContext.getInstance().setProperty("net.sf.jasperreports.compiler.temp.dir", "/tmp")
    }

    override fun type(): TimesheetDocument.Type {
        return TimesheetDocument.Type.PDF_V1
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val values = createValuesForLayoutParams(exportParams, timesheet)
        val outputStream = ByteArrayOutputStream()

        template("${type()}/timesheet_template.jrxml").use { template ->
            outputStream.use { out ->
                val report = JasperCompileManager.compileReport(template)
                val print = JasperFillManager.fillReport(report, values, JREmptyDataSource())
                JasperExportManager.exportReportToPdfStream(print, out)
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.PDF_V1, timesheet.customer.name, timesheet.dateRange, outputStream.toByteArray()
        )
    }

    private fun createValuesForLayoutParams(exportParams: Map<String, String>, timesheet: ExportTimesheet) = mapOf(
        "name" to exportParams["contact-name"],
        "email" to exportParams["contact-email"],
        "period_start" to format(timesheet.dateRange.start),
        "period_end" to format(timesheet.dateRange.endInclusive),
        "total_working_hours" to format(timesheet.totalDuration().toDouble(DurationUnit.HOURS)),
        "timesheet_entries" to JRBeanCollectionDataSource(toDataSource(timesheet))
    )

    private fun toDataSource(timesheet: ExportTimesheet) =
        timesheet.entriesGroupedByProjectAndTaskAndTagsAndStartDate()
            .sortedWith(entryComparator())
            .map { PdfTimesheetEntry.of(it) }

    private fun entryComparator(): Comparator<ExportTimesheet.Entry> =
        compareBy({ it.dateTimeRange.start }, { it.project.name }, { format(it.tags) }, { it.duration })
}
