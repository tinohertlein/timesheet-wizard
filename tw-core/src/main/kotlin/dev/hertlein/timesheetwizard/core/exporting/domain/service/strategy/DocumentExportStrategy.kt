package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

internal abstract class DocumentExportStrategy(
    private val repositoryPort: RepositoryPort
) : ExportStrategy {

    abstract fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)

    override fun export(exportParams: Map<String, String>, timesheet: ExportTimesheet) {
        repositoryPort.save(create(exportParams, timesheet))
    }

    open fun fileNameFrom(dateRange: ClosedRange<LocalDate>): String {
        val startDate = formatLocalDate(dateRange.start)
        val endDate = formatLocalDate(dateRange.endInclusive)

        return "timesheet_$startDate-$endDate.${type().type}"
    }

    @Suppress("MagicNumber")
    private val dateFormatter = DateTimeFormatterBuilder()
        .appendValue(ChronoField.YEAR, 4, 4, SignStyle.EXCEEDS_PAD)
        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
        .appendValue(ChronoField.DAY_OF_MONTH, 2)
        .toFormatter(Locale.GERMANY)

    fun formatLocalDate(localDate: LocalDate): String = dateFormatter.format(localDate)

}