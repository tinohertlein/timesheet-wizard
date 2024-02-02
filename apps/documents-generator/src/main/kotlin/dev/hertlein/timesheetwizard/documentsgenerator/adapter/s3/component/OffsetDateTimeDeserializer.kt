package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.quarkus.jackson.ObjectMapperCustomizer
import jakarta.inject.Singleton
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Singleton
class RegisterCustomModuleCustomizer : ObjectMapperCustomizer {

    override fun customize(mapper: ObjectMapper) {
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(SimpleModule().addDeserializer(OffsetDateTime::class.java, OffsetDateTimeDeserializer()))
    }
}

class OffsetDateTimeDeserializer : JsonDeserializer<OffsetDateTime?>() {

    companion object {
        private val DATE_TIME_FORMATTER
                : DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext?):
            OffsetDateTime? = jsonParser.text?.let { OffsetDateTime.parse(jsonParser.text, DATE_TIME_FORMATTER) }
}
