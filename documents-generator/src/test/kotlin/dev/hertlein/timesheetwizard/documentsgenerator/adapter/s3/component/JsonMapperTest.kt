package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.util.TestMother.aTimesheet
import dev.hertlein.timesheetwizard.documentsgenerator.util.TestMother.emptyTimesheetJson
import dev.hertlein.timesheetwizard.documentsgenerator.util.TestMother.timesheetJson
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import jakarta.inject.Inject

@QuarkusTest
@DisplayName("JsonMapper")
internal class JsonMapperTest {

    @Inject
    lateinit var jsonMapper: JsonMapper

    @Nested
    inner class ToTimesheetEntity {

        @Test
        fun `should create timesheet instance from json`() {
            val timesheet = jsonMapper.toTimesheetEntity(timesheetJson)

            assertThat(timesheet).isEqualTo(aTimesheet())
        }

        @Test
        fun `should create timesheet instance from json without entries `() {
            val timesheet = jsonMapper.toTimesheetEntity(emptyTimesheetJson)

            assertThat(timesheet).isEqualTo(aTimesheet().copy(entries = listOf()))
        }
    }
}
