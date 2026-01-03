package dev.hertlein.timesheetwizard.app.aws

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class AwsLambdaAdapter(
    private val objectMapper: ObjectMapper,
    private val importService: ImportService
) : Consumer<String> {

    override fun accept(input: String) {
        importService.import(toInputParams(input))
    }

    private fun toInputParams(input: String): ImportParams {
        return objectMapper.readValue(input, ImportParams::class.java)
    }
}