package dev.hertlein.timesheetwizard.core.importing.domain.model

import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.time.Duration

internal data class ImportTimesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<Entry> = emptyList()
) {

    fun add(entries: List<Entry>): ImportTimesheet = copy(entries = this.entries + entries)

    internal data class Entry(
        val project: Project,
        val task: Task,
        val description: Description,
        val tags: List<Tag>,
        val dateTimeRange: DateTimeRange,
        val billable: Billable,
        val duration: Duration
    ) {

        companion object {

            @Suppress("LongParameterList")
            fun of(
                projectId: String,
                projectName: String,
                taskId: String,
                taskName: String,
                description: String,
                tags: List<String>,
                start: OffsetDateTime,
                end: OffsetDateTime,
                billable: Boolean,
                duration: Duration
            ) = Entry(
                Project(projectId, projectName),
                Task(taskId, taskName),
                Description(description),
                tags.map { Tag(it) },
                DateTimeRange(start, end),
                Billable(billable),
                duration
            )
        }

        @JvmInline
        internal value class Tag(val value: String)

        @JvmInline
        internal value class Description(val value: String)

        @JvmInline
        internal value class Billable(val value: Boolean)

        internal data class Project(val id: String, val name: String)

        internal data class Task(val id: String, val name: String)

        internal data class DateTimeRange(val start: OffsetDateTime, val end: OffsetDateTime)
    }
}
