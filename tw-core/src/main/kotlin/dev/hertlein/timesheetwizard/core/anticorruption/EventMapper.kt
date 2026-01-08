package dev.hertlein.timesheetwizard.core.anticorruption

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing.ExportingStartedEvent
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.eventing.ImportingFinishedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet

internal class EventMapper(private val eventBus: EventBus) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onImportingFinished(event: ImportingFinishedEvent) {
        val entries: List<ExportTimesheet.Entry> = mapEntries(event.timesheet.entries)
        val exportTimesheet = ExportTimesheet(mapCustomer(event.timesheet.customer), event.timesheet.dateRange, entries)
        eventBus.post(ExportingStartedEvent(exportTimesheet))
    }

    private fun mapEntries(entries: List<ImportTimesheet.Entry>): List<ExportTimesheet.Entry> {
        return entries.map { importEntry ->
            ExportTimesheet.Entry.of(
                importEntry.project.name,
                importEntry.task.name,
                importEntry.tags.map { t -> t.name },
                importEntry.dateTimeRange.start,
                importEntry.dateTimeRange.end,
                importEntry.duration
            )
        }
    }

    private fun mapCustomer(importCustomer: Customer): ExportTimesheet.Customer {
        return ExportTimesheet.Customer(importCustomer.id.value, importCustomer.name.value)
    }
}