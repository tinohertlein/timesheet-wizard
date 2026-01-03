package dev.hertlein.timesheetwizard.core.export.adapter.incoming.eventing

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.service.ExportService

internal class EventSubscribeAdapter(eventBus: EventBus, private val exportService: ExportService) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onTimesheetImported(timesheet: ExportTimesheet) {
        exportService.export(timesheet)
    }
}