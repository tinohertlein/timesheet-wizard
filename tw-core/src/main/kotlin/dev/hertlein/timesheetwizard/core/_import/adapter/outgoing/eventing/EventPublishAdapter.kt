package dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.eventing

import dev.hertlein.timesheetwizard.core._import.core.port.EventPublishPort
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class EventPublishAdapter(val eventPublisher: ApplicationEventPublisher) : EventPublishPort {

    override fun publish(timesheet: Timesheet) {
        eventPublisher.publishEvent(timesheet)
    }
}