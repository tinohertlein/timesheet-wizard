package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

internal data class ResponseBody(
    val timeentries: List<TimeEntry>,
    val totals: List<Totals>? = null
) {
    data class Totals(val totalTime: Long, val entriesCount: Long)

    data class TimeEntry(
        val projectName: String,
        val description: String,
        val tags: List<Tag>?,
        val timeInterval: TimeInterval
    ) {

        data class Tag(val name: String)

        data class TimeInterval(val start: String, val end: String, val duration: Long)
    }
}