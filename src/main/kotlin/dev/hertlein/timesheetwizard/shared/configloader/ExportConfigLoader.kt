package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.ExportStrategyConfig

interface ExportConfigLoader {

    fun loadExportConfig(customer: Customer): List<ExportStrategyConfig>
}