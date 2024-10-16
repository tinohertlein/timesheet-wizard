package dev.hertlein.timesheetwizard.import_.adapter.incoming.lambda

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.import_.core.ImportService
import dev.hertlein.timesheetwizard.import_.core.model.ImportParams
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class LambdaAdapter(
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