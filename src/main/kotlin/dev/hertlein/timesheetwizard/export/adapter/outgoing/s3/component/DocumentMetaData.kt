package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component

import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument

data class DocumentMetaData(val version: String, val suffix: String) {

    companion object {

        fun from(type: TimesheetDocument.Type): DocumentMetaData = when (type) {
            TimesheetDocument.Type.XLSX_V1 -> DocumentMetaData("v1", "xlsx")
            TimesheetDocument.Type.XLSX_V2 -> DocumentMetaData("v2", "xlsx")
            TimesheetDocument.Type.PDF_V1 -> DocumentMetaData("v1", "pdf")
            TimesheetDocument.Type.CSV_V1 -> DocumentMetaData("v1", "csv")
        }
    }
}
