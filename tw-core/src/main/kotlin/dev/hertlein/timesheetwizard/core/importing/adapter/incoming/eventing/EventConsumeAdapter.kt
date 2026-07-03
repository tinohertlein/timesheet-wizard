package dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService

data class ImportStartedEvent(val importParams: ImportParams)

internal class EventConsumeAdapter(eventBus: EventBus, private val importService: ImportService) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onImportStarted(event: ImportStartedEvent) {
        importService.import(event.importParams)
    }
}