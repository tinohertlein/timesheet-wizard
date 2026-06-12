package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType

internal interface ExportStrategy {

    fun type(): ExportType
    
    fun export(exportParams: Map<String, String>, timesheet: ExportTimesheet)

}
