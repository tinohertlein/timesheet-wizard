package dev.hertlein.timesheetwizard.core.export.domain.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal data class ExportTimesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<Entry> = emptyList()
) {

    fun isEmpty() = entries.isEmpty()

    fun totalDuration(): Duration =
        entries
            .map { it.duration }
            .fold(0.hours) { total, current -> total + current }

    fun entriesGroupedByProjectAndTaskAndTagsAndStartDate(): List<Entry> {
        return entries
            .groupBy { Grouping.Key(it.project, it.task, it.tags, it.dateTimeRange.start.truncatedTo(ChronoUnit.DAYS)) }
            .map { grouped ->
                val sumDuration = grouped.value.sumOf { it.duration.inWholeMinutes }
                Grouping(grouped.key, Grouping.Value(sumDuration.minutes))
            }
            .map {
                Entry(
                    it.key.project,
                    it.key.task,
                    it.key.tags,
                    Entry.DateTimeRange(it.key.date, it.key.date),
                    it.value.workDuration
                )
            }
    }

    private class Grouping(
        val key: Key,
        val value: Value
    ) {
        data class Key(
            val project: Entry.Project,
            val task: Entry.Task,
            val tags: List<Entry.Tag>,
            val date: OffsetDateTime
        )

        data class Value(
            val workDuration: Duration
        )
    }

    internal data class Customer(val id: String, val name: String)

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
