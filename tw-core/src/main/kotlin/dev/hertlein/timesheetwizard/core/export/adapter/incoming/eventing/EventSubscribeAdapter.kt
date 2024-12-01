package dev.hertlein.timesheetwizard.core.export.adapter.incoming.eventing

import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.service.ExportService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
internal class EventSubscribeAdapter(private val exportService: ExportService) {

    @EventListener
    fun onTimesheetImported(timesheet: ExportTimesheet) {
        exportService.export(timesheet)
    }
}