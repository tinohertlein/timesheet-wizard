package dev.hertlein.timesheetwizard.importer.model

import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.time.Duration

@Introspected
data class Timesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<TimesheetEntry> = emptyList()
) {
    fun add(entries: List<TimesheetEntry>): Timesheet = copy(entries = this.entries + entries)
    fun add(vararg entry: TimesheetEntry): Timesheet = add(entry.toList())
}

@Introspected
data class TimesheetEntry(
    val project: Project,
    val task: Task,
    val tags: List<Tag>,
    val dateTimeRange: DateTimeRange,
    val duration: Duration
) {
    companion object {

        @Suppress("LongParameterList")
        fun of(
            project: String,
            task: String,
            tags: List<String>,
            start: OffsetDateTime,
            end: OffsetDateTime,
            duration: Duration
        ) = TimesheetEntry(
            Project(project),
            Task(task),
            tags.map { Tag(it) },
            DateTimeRange(start, end),
            duration
        )
    }
}

@Introspected
@JvmInline
value class Task(val name: String)

@Introspected
@JvmInline
value class Tag(val name: String)

@Introspected
@JvmInline
value class Project(val name: String)

@Introspected
data class DateTimeRange(val start: OffsetDateTime, val end: OffsetDateTime)
