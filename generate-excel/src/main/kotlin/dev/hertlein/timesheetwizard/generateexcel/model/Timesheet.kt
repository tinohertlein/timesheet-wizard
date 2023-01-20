package dev.hertlein.timesheetwizard.generateexcel.model

import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class Timesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<TimesheetEntry> = emptyList()
) {
    fun totalDuration(): Duration =
        entries
            .map { it.duration }
            .fold(0.toDuration(DurationUnit.HOURS)) { total, current -> total + current }
}

data class TimesheetEntry(
    val project: Project,
    val task: Task,
    val tags: List<Tag>,
    val date: LocalDate,
    val duration: Duration
)

@JvmInline
value class Task(val name: String)

@JvmInline
value class Tag(val name: String)

@JvmInline
value class Project(val name: String)

