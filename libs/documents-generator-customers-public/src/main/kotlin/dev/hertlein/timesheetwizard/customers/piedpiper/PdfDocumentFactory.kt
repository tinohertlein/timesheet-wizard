package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.documentsgenerator.spi.TimesheetDocumentFactory
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument
import jakarta.inject.Singleton
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import java.io.ByteArrayOutputStream
import kotlin.time.DurationUnit

@Singleton
class PdfDocumentFactory : TimesheetDocumentFactory, PiedPiperConfig() {

    @Suppress("ConstructorParameterNaming")
    data class PdfTimesheetEntry(
        val date: String, val project: String, val location: String, val activity: String, val working_hours: String
    ) {

        companion object {
            fun of(entry: Timesheet.Entry): PdfTimesheetEntry {
                return PdfTimesheetEntry(
                    format(entry.start),
                    format(entry.project),
                    format(entry.tags),
                    format(entry.task),
                    format(entry.duration.toDouble(DurationUnit.HOURS))
                )
            }
        }
    }

    override fun create(contact: ContactDetails, timesheet: Timesheet): TimesheetDocument {
        val values = createValuesForLayoutParams(contact, timesheet)
        val outputStream = ByteArrayOutputStream()

        template("piedpiper/timesheet_template.jrxml").use { template ->
            outputStream.use { out ->
                val report = JasperCompileManager.compileReport(template)
                val print = JasperFillManager.fillReport(report, values, JREmptyDataSource())
                JasperExportManager.exportReportToPdfStream(print, out)
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.PDF, timesheet.customer, timesheet.dateRange, outputStream.toByteArray()
        )
    }

    private fun createValuesForLayoutParams(contact: ContactDetails, timesheet: Timesheet) = mapOf(
        "name" to contact.name.value,
        "email" to contact.email.value,
        "period_start" to format(timesheet.dateRange.start),
        "period_end" to format(timesheet.dateRange.endInclusive),
        "total_working_hours" to format(timesheet.totalDuration().toDouble(DurationUnit.HOURS)),
        "timesheet_entries" to JRBeanCollectionDataSource(toDataSource(timesheet))
    )

    private fun toDataSource(timesheet: Timesheet) =
        timesheet.entries.sortedWith(entryComparator()).map { PdfTimesheetEntry.of(it) }

    private fun entryComparator(): Comparator<Timesheet.Entry> =
        compareBy({ it.start }, { it.project.name }, { format(it.tags) }, { it.duration })
}
