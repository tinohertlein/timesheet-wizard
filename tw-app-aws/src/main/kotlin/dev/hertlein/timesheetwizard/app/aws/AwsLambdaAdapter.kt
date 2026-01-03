package dev.hertlein.timesheetwizard.app.aws

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class AwsLambdaAdapter(
    private val objectMapper: ObjectMapper,
    private val eventBus: EventBus
) : Consumer<String> {

    override fun accept(input: String) {
        eventBus.post(toInputParams(input))
    }

    private fun toInputParams(input: String): ImportParams {
        return objectMapper.readValue(input, ImportParams::class.java)
    }
}