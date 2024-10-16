package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.ExportConfig

interface ExportConfigLoader {

    fun loadExportConfig(): ExportConfig
}