package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import jakarta.inject.Singleton
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Singleton
class ObjectMapperBeanEventListener : BeanCreatedEventListener<ObjectMapper> {

    override fun onCreated(event: BeanCreatedEvent<ObjectMapper>): ObjectMapper {
        return event.bean.apply {
            registerModule(SimpleModule().addSerializer(OffsetDateTime::class.java, OffsetDateTimeSerializer()))
        }
    }
}

class OffsetDateTimeSerializer : JsonSerializer<OffsetDateTime?>() {

    companion object {
        private val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    }

    override fun serialize(value: OffsetDateTime?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(DATE_TIME_FORMATTER.format(value));
    }
}