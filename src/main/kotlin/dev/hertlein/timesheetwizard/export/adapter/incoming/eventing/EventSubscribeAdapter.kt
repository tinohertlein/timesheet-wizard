package dev.hertlein.timesheetwizard.export.adapter.incoming.eventing

import dev.hertlein.timesheetwizard.export.core.ExportService
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EventSubscribeAdapter(private val exportService: ExportService) {

    @EventListener
    fun onTimesheetImported(timesheet: Timesheet) {
        exportService.export(timesheet)
    }
}