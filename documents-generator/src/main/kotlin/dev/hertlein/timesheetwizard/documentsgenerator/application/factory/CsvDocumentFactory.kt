package dev.hertlein.timesheetwizard.documentsgenerator.application.factory

import com.opencsv.CSVWriter
import dev.hertlein.timesheetwizard.documentsgenerator.model.Project
import dev.hertlein.timesheetwizard.documentsgenerator.model.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.model.Task
import dev.hertlein.timesheetwizard.documentsgenerator.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetDocument
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetEntry
import jakarta.inject.Singleton
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
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
class CsvDocumentFactory : TimesheetDocumentFactory {

    override fun apply(timesheet: Timesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()
        val csv = toCsv(timesheet)

        outputStream.use { bos ->
            OutputStreamWriter(bos).use { osw ->
                CSVWriter(osw).use { csvw -> csvw.writeAll(csv) }
            }
        }

        return TimesheetDocument(
            TimesheetDocument.Type.CSV,
            timesheet.customer,
            timesheet.dateRange,
            outputStream.toByteArray()
        )
    }

    private fun toCsv(timesheet: Timesheet): List<Array<String>> = timesheet
        .entries
        .sortedWith(entryComparator())
        .map {
            arrayOf(
                it.date.format(),
                it.project.format(),
                it.tags.format(),
                it.task.format(),
                it.duration.toDouble(DurationUnit.HOURS).format()
            )
        }

    private fun entryComparator(): Comparator<TimesheetEntry> =
        compareBy({ it.date }, { it.project.name }, { it.tags.format() }, { it.duration })
}
