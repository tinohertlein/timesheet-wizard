package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.eventing

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.importing.domain.port.EventPublishPort

internal data class ImportingFinishedEvent(val timesheet: ImportTimesheet)

internal class EventPublishAdapter(private val eventBus: EventBus) : EventPublishPort {

    override fun publish(timesheet: ImportTimesheet) {
        eventBus.post(ImportingFinishedEvent(timesheet))
    }
}