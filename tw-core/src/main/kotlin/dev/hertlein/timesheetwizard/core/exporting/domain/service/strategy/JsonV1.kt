package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal class JsonV1(repositoryPort: RepositoryPort, private val objectMapper: JsonMapper) : DocumentExportStrategy(repositoryPort) {

    companion object {

        private val timezone = ZoneId.of("Europe/Berlin")
        private val locale: Locale = Locale.GERMAN

        private fun format(tags: List<ExportTimesheet.Entry.Tag>) = tags.joinToString(" ") { it.name }
        private fun format(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", locale))
        private fun format(time: LocalTime): String = time.format(DateTimeFormatter.ofPattern("H:mm", locale))
        private fun format(time: OffsetDateTime): String = format(time.atZoneSameInstant(timezone).toLocalTime())
    }

    override fun type(): ExportType {
        return ExportType.JSON_V1
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val entries =
            timesheet.entries.map {
                Entry(
                    format(it.dateTimeRange.start.toLocalDate()),
                    format(it.dateTimeRange.start),
                    format(it.dateTimeRange.end),
                    format(it.tags)
                )
            }

        val json = objectMapper.writeValueAsString(entries)
        return TimesheetDocument(
            ExportType.JSON_V1,
            timesheet.customer.name,
            timesheet.dateRange,
            json.toByteArray()
        )
    }

    internal data class Entry(val datum: String, val von: String, val bis: String, val ort: String)
}