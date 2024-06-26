package dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet

import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

data class Timesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<Entry> = emptyList()
) {

    fun isEmpty() = entries.isEmpty()

    fun totalDuration(): Duration =
        entries
            .map { it.duration }
            .fold(0.hours) { total, current -> total + current }

    data class Entry(
        val project: Project,
        val task: Task,
        val tags: List<Tag>,
        val start: OffsetDateTime,
        val end: OffsetDateTime,
        val duration: Duration
    ) {

        @JvmInline
        value class Task(val name: String)

        @JvmInline
        value class Tag(val name: String)

        @JvmInline
        value class Project(val name: String)
    }
}
