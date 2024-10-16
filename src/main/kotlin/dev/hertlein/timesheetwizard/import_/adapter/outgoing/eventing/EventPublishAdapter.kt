package dev.hertlein.timesheetwizard.import_.adapter.outgoing.eventing

import dev.hertlein.timesheetwizard.import_.core.port.EventPublishPort
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventPublishAdapter(val eventPublisher: ApplicationEventPublisher) : EventPublishPort {

    override fun publish(timesheet: Timesheet) {
        eventPublisher.publishEvent(timesheet)
    }
}