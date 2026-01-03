package dev.hertlein.timesheetwizard.core.util

import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours

internal object TestFixture {

    object App {
        val repositoryInMemory = RepositoryInMemory()
        val objectMapper = Core.objectMapper
    }

    object Export {

        val aZoneOffset: ZoneOffset = ZoneOffset.ofHours(1)

        private val aStart: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)

        private val anEnd: OffsetDateTime = OffsetDateTime.of(2022, 1, 1, 10, 0, 0, 0, aZoneOffset)

        private val aDateRange = LocalDate.of(2022, 1, 1)..LocalDate.of(2022, 12, 31)

        private val aCustomer = ExportTimesheet.Customer("a-customer-id", "a-customer-name")

        private val aProject = ExportTimesheet.Entry.Project("a-project")

        private val aTask = ExportTimesheet.Entry.Task("a-task")

        private val someTags = listOf("a-tag").map { ExportTimesheet.Entry.Tag(it) }

        private val aDateTimeRange = ExportTimesheet.Entry.DateTimeRange(aStart, anEnd)

        val anEntry =
            ExportTimesheet.Entry(
                aProject,
                aTask,
                someTags,
                aDateTimeRange,
                2.hours
            )

        fun aTimesheet(vararg entries: ExportTimesheet.Entry) =
            aTimesheet(entries.toList())

        fun aTimesheet(entries: List<ExportTimesheet.Entry>) =
            ExportTimesheet(aCustomer, aDateRange, entries)
    }

}