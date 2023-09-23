package dev.hertlein.timesheetwizard.generateexports.adapter.s3.component

import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument

data class DocumentMetaData(val prefix: String, val suffix: String) {

    companion object {

        val EXCEL = of(TimesheetDocument.Type.EXCEL)

        fun of(type: TimesheetDocument.Type): DocumentMetaData {
            return when (type) {
                TimesheetDocument.Type.EXCEL -> DocumentMetaData("xlsx", "xlsx")
            }
        }
    }
}
