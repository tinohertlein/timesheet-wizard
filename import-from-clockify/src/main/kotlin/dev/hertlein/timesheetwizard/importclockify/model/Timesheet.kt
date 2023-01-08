package dev.hertlein.timesheetwizard.importclockify.model

import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import kotlin.time.Duration

@Introspected
data class Timesheet(
    val customer: Customer,
    val dateRange: ClosedRange<LocalDate>,
    val entries: List<TimesheetEntry> = emptyList()
) {
    fun add(entries: List<TimesheetEntry>): Timesheet = copy(entries = aggregate(this.entries + entries))
    fun add(vararg entry: TimesheetEntry): Timesheet = add(entry.toList())

    private data class GroupingKey(
        val projectName: String,
        val taskName: String,
        val tags: List<String>,
        val date: LocalDate
    )

    private fun aggregate(entries: List<TimesheetEntry>): List<TimesheetEntry> =
        entries
            .groupingBy { entry ->
                GroupingKey(
                    entry.project.name,
                    entry.task.name,
                    entry.tags.map { it.name },
                    entry.date
                )
            }
            .aggregate { _, acc: TimesheetEntry?, element, isFirst ->
                TimesheetEntry(
                    element.project,
                    element.task,
                    element.tags,
                    element.date,
                    if (isFirst) element.duration else element.duration.plus(acc!!.duration)
                )
            }
            .values
            .toList()
}

@Introspected
data class TimesheetEntry(
    val project: Project,
    val task: Task,
    val tags: List<Tag>,
    val date: LocalDate,
    val duration: Duration
) {
    constructor(
        project: String,
        task: String,
        tags: List<String>,
        date: LocalDate,
        duration: Duration
    ) : this(Project(project), Task(task), tags.map { Tag(it) }, date, duration)
}

@Introspected
data class Task(val name: String)
@Introspected
data class Tag(val name: String)
@Introspected
data class Project(val name: String)

