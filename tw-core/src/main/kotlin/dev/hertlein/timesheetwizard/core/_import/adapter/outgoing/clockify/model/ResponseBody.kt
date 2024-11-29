package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.model

internal data class ResponseBody(
    val timeentries: List<TimeEntry>,
    val totals: List<Totals>? = null
) {
    internal data class Totals(val totalTime: Long, val entriesCount: Long)

    internal data class TimeEntry(
        val projectName: String,
        val description: String,
        val tags: List<Tag>?,
        val timeInterval: TimeInterval
    ) {

        internal data class Tag(val name: String) {
            // Empty constructor is needed for com.fasterxml.jackson.module:jackson-module-kotlin:2.18.1
            constructor() : this("")
        }

        internal data class TimeInterval(val start: String, val end: String, val duration: Long)
    }
}