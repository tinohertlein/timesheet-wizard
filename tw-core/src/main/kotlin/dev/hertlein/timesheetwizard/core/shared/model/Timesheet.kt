package dev.hertlein.timesheetwizard.core.shared.model

import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal data class Timesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<Entry> = emptyList()
) {

    fun add(entries: List<Entry>): Timesheet = copy(entries = this.entries + entries)

    fun add(vararg entry: Entry): Timesheet = add(entry.toList())

    fun isEmpty() = entries.isEmpty()

    fun totalDuration(): Duration =
        entries
            .map { it.duration }
            .fold(0.hours) { total, current -> total + current }

    internal data class Entry(
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
            ) = Entry(
                Project(project),
                Task(task),
                tags.map { Tag(it) },
                DateTimeRange(start, end),
                duration
            )
        }

        @JvmInline
        internal value class Task(val name: String)

        @JvmInline
        internal value class Tag(val name: String)

        @JvmInline
        internal value class Project(val name: String)

        internal data class DateTimeRange(val start: OffsetDateTime, val end: OffsetDateTime)
    }
}
