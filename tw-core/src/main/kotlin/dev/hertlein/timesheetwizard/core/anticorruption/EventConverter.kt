package dev.hertlein.timesheetwizard.core.anticorruption

import com.google.common.base.Converter
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing.ExportingStartedEvent
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet.*
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.eventing.ImportingFinishedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet

internal class EventConverter(private val eventBus: EventBus) : Converter<ImportTimesheet, ExportTimesheet>() {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onImportingFinished(event: ImportingFinishedEvent) {
        eventBus.post(ExportingStartedEvent(doForward(event.timesheet)))
    }

    override fun doForward(importTimesheet: ImportTimesheet): ExportTimesheet =
        ExportTimesheet(convertCustomer(importTimesheet.customer), importTimesheet.dateRange, convertEntries(importTimesheet.entries))

    override fun doBackward(b: ExportTimesheet): ImportTimesheet {
        TODO("Not yet implemented")
    }

    private fun convertEntries(entries: List<ImportTimesheet.Entry>): List<Entry> = entries.map { importEntry ->
        Entry.of(
            importEntry.project.name,
            importEntry.task.name,
            importEntry.tags.map { t -> t.name },
            importEntry.dateTimeRange.start,
            importEntry.dateTimeRange.end,
            importEntry.duration
        )
    }

    private fun convertCustomer(importCustomer: Customer): ExportTimesheet.Customer = Customer(importCustomer.id.value, importCustomer.name.value)
}