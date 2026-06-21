package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType


internal data class DocumentMetaData(val version: String, val suffix: String) {

    companion object {

        fun from(exportType: ExportType): DocumentMetaData = when (exportType) {
            ExportType.XLSX_V1 -> DocumentMetaData("v1", "xlsx")
            ExportType.XLSX_V2 -> DocumentMetaData("v2", "xlsx")
            ExportType.XLSX_V3 -> DocumentMetaData("v3", "xlsx")
            ExportType.PDF_V1 -> DocumentMetaData("v1", "pdf")
            ExportType.CSV_V1 -> DocumentMetaData("v1", "csv")
            ExportType.JSON_V1 -> DocumentMetaData("v1", "json")
        }
    }
}
