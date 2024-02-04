package dev.hertlein.timesheetwizard.customers.piedpiper

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Id
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer.Name
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Project
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Tag
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet.Entry.Task
import java.io.InputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.Locale

abstract class PiedPiperConfig {

    companion object {

        fun format(project: Project) = project.name
        fun format(task: Task) = task.name
        fun format(tags: List<Tag>) = tags.joinToString(" ") { it.name }
        fun format(date: LocalDate): String = date.format(java.time.format.DateTimeFormatter.ISO_DATE)
        fun format(dateTime: OffsetDateTime): String = format(dateTime.toLocalDate())
        fun format(double: Double): String = DecimalFormat("0.00", DecimalFormatSymbols(Locale.GERMANY)).format(double)

        fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
    }

    fun canHandle(customer: Customer) = customer == Customer(Id("1000"), Name("PiedPiper"))
}
