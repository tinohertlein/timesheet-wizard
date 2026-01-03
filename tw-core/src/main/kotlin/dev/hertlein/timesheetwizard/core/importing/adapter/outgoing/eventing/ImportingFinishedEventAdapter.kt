package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.eventing

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.importing.domain.port.ImportingFinishedEventPort

internal class ImportingFinishedEventAdapter(private val eventBus: EventBus) : ImportingFinishedEventPort {

    override fun publish(timesheet: ImportTimesheet) {
        eventBus.post(timesheet)
    }
}