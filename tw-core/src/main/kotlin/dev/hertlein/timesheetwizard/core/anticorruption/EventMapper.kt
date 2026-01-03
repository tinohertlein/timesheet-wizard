package dev.hertlein.timesheetwizard.core.anticorruption

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import dev.hertlein.timesheetwizard.core._import.domain.model.Customer
import dev.hertlein.timesheetwizard.core._import.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet

internal class EventMapper(private val eventBus: EventBus) {

    init {
        eventBus.register(this)
    }

    @Subscribe
    fun onTimesheetImported(importTimesheet: ImportTimesheet) {
        val entries: List<ExportTimesheet.Entry> = mapEntries(importTimesheet.entries)
        val exportTimesheet = ExportTimesheet(mapCustomer(importTimesheet.customer), importTimesheet.dateRange, entries)
        eventBus.post(exportTimesheet)
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