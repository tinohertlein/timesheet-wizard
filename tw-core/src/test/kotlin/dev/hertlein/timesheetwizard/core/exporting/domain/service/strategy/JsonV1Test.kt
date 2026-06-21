package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aZoneOffset
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.anEntry
import io.mockk.mockk
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.time.OffsetDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("JsonV1")
class JsonV1Test {

    val objectMapper: JsonMapper = JsonMapper.builder()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, false)
        .addModule(kotlinModule())
        .build()

    @Test
    fun `should create a json document from timesheet`() {
        val workDurationHours = 2L

        val workStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)
        val workEnd = workStart.plusHours(workDurationHours)
        val work = anEntry.copy(
            duration = workDurationHours.toDuration(DurationUnit.HOURS),
            dateTimeRange = ExportTimesheet.Entry.DateTimeRange(workStart, workEnd)
        )
        val timesheet = aTimesheet(work)
        val expected =
            ResourcesReader.stringFromResourceFile("${this.javaClass.packageName}/timesheet_v1_PiedPiper_20220101-20221231.json")

        val actual =
            JsonV1(mockk(relaxed = true), objectMapper).create(emptyMap(), timesheet)

        assertSoftly { softly ->
            softly.assertThat(actual.exportType).isEqualTo(ExportType.JSON_V1)
            softly.assertThat(actual.fileName).isEqualTo("timesheet.json")
            softly.assertThat(actual.customerName).isEqualTo(timesheet.customer.name)
            softly.assertThat(actual.dateRange).isEqualTo(timesheet.dateRange)
            softly.assertThat(String(actual.content)).isEqualTo(expected)
        }
    }
}