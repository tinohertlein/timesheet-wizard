package dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.service.ExportService

internal data class ExportingStartedEvent(val timesheet: ExportTimesheet)

internal class EventConsumeAdapter(eventBus: EventBus, private val exportService: ExportService) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onExportingStarted(event: ExportingStartedEvent) {
        exportService.export(event.timesheet)
    }
}