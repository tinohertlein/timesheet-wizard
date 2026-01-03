package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.eventing

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core._import.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core._import.domain.port.EventPublishPort

internal class EventPublishAdapter(private val eventBus: EventBus) : EventPublishPort {

    override fun publish(timesheet: ImportTimesheet) {
        eventBus.post(timesheet)
    }
}