package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument


internal data class DocumentMetaData(val version: String, val suffix: String) {

    companion object {

        fun from(type: TimesheetDocument.Type): DocumentMetaData = when (type) {
            TimesheetDocument.Type.XLSX_V1 -> DocumentMetaData("v1", "xlsx")
            TimesheetDocument.Type.XLSX_V2 -> DocumentMetaData("v2", "xlsx")
            TimesheetDocument.Type.PDF_V1 -> DocumentMetaData("v1", "pdf")
            TimesheetDocument.Type.CSV_V1 -> DocumentMetaData("v1", "csv")
        }
    }
}
