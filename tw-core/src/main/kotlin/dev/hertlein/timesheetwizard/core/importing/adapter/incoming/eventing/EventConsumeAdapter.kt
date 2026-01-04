package dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService

internal class EventConsumeAdapter(eventBus: EventBus, private val importService: ImportService) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onImportingStarted(importParams: ImportParams) {
        importService.import(importParams)
    }
}