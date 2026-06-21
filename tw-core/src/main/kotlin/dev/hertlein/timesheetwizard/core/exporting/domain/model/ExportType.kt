package dev.hertlein.timesheetwizard.core.exporting.domain.model

internal enum class ExportType(val version: String, val type: String) {
    XLSX_V1("v1", "xlsx"),
    XLSX_V2("v2", "xlsx"),
    XLSX_V3("v3", "xlsx"),
    PDF_V1("v1", "pdf"),
    CSV_V1("v1", "csv"),
    JSON_V1("v1", "json")
}