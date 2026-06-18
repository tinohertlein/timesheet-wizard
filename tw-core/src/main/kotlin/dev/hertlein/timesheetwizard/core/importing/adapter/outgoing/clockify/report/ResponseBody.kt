package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report

internal data class ResponseBody(
    val timeentries: List<TimeEntry>? = null,
    val totals: List<Totals?>? = null
) {
    data class Totals(val totalTime: Long, val entriesCount: Long)

    data class TimeEntry(
        val projectId: String,
        val projectName: String,
        val taskId: String? = null,
        val taskName: String? = null,
        val description: String,
        val tags: List<Tag>?,
        val billable: Boolean,
        val timeInterval: TimeInterval,
    ) {

        data class Tag(val name: String)

        data class TimeInterval(val start: String, val end: String, val duration: Long)
    }
}