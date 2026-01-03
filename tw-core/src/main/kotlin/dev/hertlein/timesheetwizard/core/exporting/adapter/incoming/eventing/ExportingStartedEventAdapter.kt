package dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.service.ExportService

internal class ExportingStartedEventAdapter(eventBus: EventBus, private val exportService: ExportService) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onExportingStarted(timesheet: ExportTimesheet) {
        exportService.export(timesheet)
    }
}