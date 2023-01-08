package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model

import io.micronaut.core.annotation.Introspected


@Introspected
data class ResponseBody(
    val timeentries: List<TimeEntry>,
    val totals: List<Totals>? = null
)  {

    @Introspected
    data class Totals(val totalTime: Long, val entriesCount: Long)

    @Introspected
    data class TimeEntry(
        val projectName: String,
        val description: String,
        val tags: List<Tag>,
        val timeInterval: TimeInterval
    )  {

        @Introspected
        data class Tag(val name: String)

        @Introspected
        data class TimeInterval(val start: String, val end: String, val duration: Long)
    }
}
