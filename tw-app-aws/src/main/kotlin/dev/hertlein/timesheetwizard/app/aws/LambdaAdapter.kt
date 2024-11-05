package dev.hertlein.timesheetwizard.app.aws

import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core._import.core.ImportService
import dev.hertlein.timesheetwizard.core._import.core.model.ImportParams
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