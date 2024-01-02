package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument


data class DocumentMetaData(val suffix: String) {

    companion object {

        fun of(type: TimesheetDocument.Type): DocumentMetaData = when (type) {
            TimesheetDocument.Type.XLSX -> DocumentMetaData("xlsx")
            TimesheetDocument.Type.PDF -> DocumentMetaData("pdf")
            TimesheetDocument.Type.CSV -> DocumentMetaData("csv")
        }
    }
}
