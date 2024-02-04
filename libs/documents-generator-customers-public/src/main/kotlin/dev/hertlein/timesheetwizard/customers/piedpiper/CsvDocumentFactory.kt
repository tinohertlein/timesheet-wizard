package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.documentsgenerator.spi.TimesheetDocumentFactory
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument
import jakarta.inject.Singleton
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import kotlin.time.DurationUnit

@Singleton
class CsvDocumentFactory : TimesheetDocumentFactory, PiedPiperConfig() {

    override fun create(contact: ContactDetails, timesheet: Timesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()
        val csv = toCsv(timesheet)
        outputStream.use { bos ->
            OutputStreamWriter(bos).use { osw ->
                com.opencsv.CSVWriter(osw).use { csvw -> csvw.writeAll(csv) }
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.CSV, timesheet.customer, timesheet.dateRange, outputStream.toByteArray()
        )
    }

    private fun toCsv(timesheet: Timesheet): List<Array<String>> = timesheet.entries.sortedWith(entryComparator()).map {
            arrayOf(
                format(it.start),
                format(it.project),
                format(it.tags),
                format(it.task),
                format(it.duration.toDouble(DurationUnit.HOURS))
            )
        }

    private fun entryComparator(): Comparator<Entry> =
        compareBy({ it.start }, { it.project.name }, { format(it.tags) }, { it.duration })
}
