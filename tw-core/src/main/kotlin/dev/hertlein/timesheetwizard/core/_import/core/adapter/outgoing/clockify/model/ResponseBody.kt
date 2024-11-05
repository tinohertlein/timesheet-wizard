package dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.model

internal data  class ResponseBody(
    val timeentries: List<TimeEntry>,
    val totals: List<Totals>? = null
) {

   internal data  class Totals(val totalTime: Long, val entriesCount: Long)

   internal data  class TimeEntry(
        val projectName: String,
        val description: String,
        val tags: List<Tag>?,
        val timeInterval: TimeInterval
    ) {

       internal data  class Tag(val name: String)

       internal data  class TimeInterval(val start: String, val end: String, val duration: Long)
    }
}