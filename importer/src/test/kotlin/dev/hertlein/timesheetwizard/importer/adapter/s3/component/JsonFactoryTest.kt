package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import dev.hertlein.timesheetwizard.importer.util.TestMother.aTimesheet
import dev.hertlein.timesheetwizard.importer.util.TestMother.timesheetJson
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("JsonFactory")
@MicronautTest
internal class JsonFactoryTest {

    @Inject
    lateinit var jsonFactory: JsonFactory

    @Nested
    inner class Create {
        @Test
        fun `should create json from timesheet instance`() {
            val timesheet = aTimesheet()

            val json = jsonFactory.create(timesheet)

            assertThat(json).isEqualToIgnoringWhitespace(timesheetJson)
        }
    }
}
