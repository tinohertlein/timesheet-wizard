package dev.hertlein.timesheetwizard.generateexports.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument

data class DocumentMetaData(val prefix: String, val suffix: String) {
    constructor(prefixAndSuffix: String) : this(prefixAndSuffix, prefixAndSuffix)

    companion object {

        val EXCEL = of(TimesheetDocument.Type.EXCEL)

        fun of(type: TimesheetDocument.Type): DocumentMetaData = when (type) {
            TimesheetDocument.Type.EXCEL -> DocumentMetaData("xlsx")
            TimesheetDocument.Type.PDF -> DocumentMetaData("pdf")
        }
    }
}
