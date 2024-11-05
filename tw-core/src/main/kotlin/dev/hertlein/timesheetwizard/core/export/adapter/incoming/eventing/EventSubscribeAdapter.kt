package dev.hertlein.timesheetwizard.core.export.adapter.incoming.eventing

import dev.hertlein.timesheetwizard.core.export.core.ExportService
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class EventSubscribeAdapter(private val exportService: ExportService) {

    @EventListener
    fun onTimesheetImported(timesheet: Timesheet) {
        exportService.export(timesheet)
    }
}