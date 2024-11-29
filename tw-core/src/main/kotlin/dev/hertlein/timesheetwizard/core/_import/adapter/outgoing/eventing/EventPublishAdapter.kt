package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.eventing

import dev.hertlein.timesheetwizard.core._import.core.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core._import.core.port.EventPublishPort
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class EventPublishAdapter(val eventPublisher: ApplicationEventPublisher) : EventPublishPort {

    override fun publish(timesheet: ImportTimesheet) {
        eventPublisher.publishEvent(timesheet)
    }
}