package dev.hertlein.timesheetwizard.core.export.core.strategy

import com.opencsv.CSVWriter
import com.opencsv.ICSVWriter
import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Component
internal class CsvV1 : ExportStrategy {

    companion object {

        const val TASK_BREAK = "Pause"

        private val timezone = ZoneId.of("Europe/Berlin")
        private val locale: Locale = Locale.GERMAN

        private const val CATEGORY = ""
        private const val DESCRIPTION = ""

        private fun format(project: Timesheet.Entry.Project) = project.name
        private fun format(date: LocalDate): String = date.format(DateTimeFormatter.ISO_DATE)
        private fun format(time: LocalTime): String = time.format(
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
        )

        private fun format(time: OffsetDateTime): String = format(time.atZoneSameInstant(timezone).toLocalTime())
        private fun format(duration: Duration): String =
            duration.toComponents { hours, minutes, _, _ -> String.format(locale, "%02d:%02d", hours, minutes) }
    }

   internal data  class CsvEntryKey(
        val login: String,
        val project: Timesheet.Entry.Project,
        val category: String,
        val description: String,
        val date: LocalDate
    ) {
        companion object {
            fun of(
                project: Timesheet.Entry.Project,
                startTime: OffsetDateTime,
                login: String
            ) = CsvEntryKey(
                login,
                project,
                CATEGORY,
                DESCRIPTION,
                startTime.toLocalDate()
            )
        }
    }

   internal data  class CsvEntryValue(
        val startTime: OffsetDateTime,
        val endTime: OffsetDateTime,
        val workDuration: Duration,
        val breakDuration: Duration
    )

   internal data  class CsvEntry(
        val key: CsvEntryKey,
        val value: CsvEntryValue
    )

    private val headings = arrayOf(
        "Login",
        "Datum",
        "Startzeit",
        "Endzeit",
        "Arbeitsdauer",
        "Pausendauer",
        "Projekt",
        "Kategorie",
        "Beschreibung"
    )

    private val separatorChar = ';'

    override fun type(): TimesheetDocument.Type {
        return TimesheetDocument.Type.CSV_V1
    }

    override fun create(exportParams: Map<String, String>, timesheet: Timesheet): TimesheetDocument {
        val outputStream = ByteArrayOutputStream()
        outputStream.use { bos ->
            OutputStreamWriter(bos, Charsets.UTF_8).use { osw ->
                CSVWriter(
                    osw,
                    separatorChar,
                    ICSVWriter.DEFAULT_QUOTE_CHARACTER,
                    ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    ICSVWriter.DEFAULT_LINE_END
                ).use { csvw -> csvw.writeAll(toCsv(exportParams, timesheet), false) }
            }
        }
        return TimesheetDocument(
            TimesheetDocument.Type.CSV_V1, timesheet.customer, timesheet.dateRange, outputStream.toByteArray()
        )
    }

    private fun toCsv(exportParams: Map<String, String>, timesheet: Timesheet): List<Array<String>> =
        listOf(headings) + toCsvRows(exportParams, timesheet.entries)

    private fun toCsvRows(exportParams: Map<String, String>, entries: List<Timesheet.Entry>): List<Array<String>> {
        return entries
            .groupBy { CsvEntryKey.of(it.project, it.dateTimeRange.start, getLogin(exportParams)) }
            .map { groupedEntry ->
                val minStart = groupedEntry.value.minOf { it.dateTimeRange.start }
                val maxEnd = groupedEntry.value.maxOf { it.dateTimeRange.end }
                val (breakSlots, workSlots) = groupedEntry.value.partition { it.task.name == TASK_BREAK }
                val breakDuration = breakSlots.sumOf { it.duration.inWholeMinutes }
                val workDuration = workSlots.sumOf { it.duration.inWholeMinutes }

                val value = CsvEntryValue(
                    minStart,
                    maxEnd,
                    workDuration.minutes,
                    breakDuration.minutes
                )
                CsvEntry(groupedEntry.key, value)
            }
            .sortedWith(csvEntryComparator())
            .map { toCsvRow(it) }
    }

    private fun getLogin(exportParams: Map<String, String>): String {
        return exportParams["login"]!!
    }

    private fun toCsvRow(entry: CsvEntry): Array<String> {
        return arrayOf(
            entry.key.login,
            format(entry.key.date),
            format(entry.value.startTime),
            format(entry.value.endTime),
            format(entry.value.workDuration),
            format(entry.value.breakDuration),
            format(entry.key.project),
            entry.key.category,
            entry.key.description
        )
    }

    private fun csvEntryComparator(): Comparator<CsvEntry> = compareBy({ it.key.date }, { it.key.project.name })
}