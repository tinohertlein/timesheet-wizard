package dev.hertlein.timesheetwizard.core.shared.configloader

import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.ExportStrategyConfig

internal interface ExportConfigLoader {

    fun loadExportConfig(customer: Customer): List<ExportStrategyConfig>
}