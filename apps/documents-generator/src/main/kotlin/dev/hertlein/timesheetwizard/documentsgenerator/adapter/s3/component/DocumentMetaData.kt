package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.TimesheetDocument

data class DocumentMetaData(val prefix: String, val suffix: String) {
    constructor(prefixAndSuffix: String) : this(prefixAndSuffix, prefixAndSuffix)

    companion object {

        val EXCEL = of(TimesheetDocument.Type.EXCEL)

        fun of(type: TimesheetDocument.Type): DocumentMetaData = when (type) {
            TimesheetDocument.Type.EXCEL -> DocumentMetaData("xlsx")
            TimesheetDocument.Type.PDF -> DocumentMetaData("pdf")
            TimesheetDocument.Type.CSV -> DocumentMetaData("csv")
        }
    }
}
