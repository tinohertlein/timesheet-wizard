package dev.hertlein.timesheetwizard.import_.adapter.incoming.azurefunction

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import dev.hertlein.timesheetwizard.import_.core.ImportService
import dev.hertlein.timesheetwizard.import_.core.model.ImportParams
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger {}


@Component
class AzureFunctionAdapter(
    private val objectMapper: ObjectMapper,
    private val importService: ImportService
) {

    @FunctionName("import")
    fun import(
        @HttpTrigger(name = "req", authLevel = AuthorizationLevel.ANONYMOUS)
        request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext
    ) {
        val body = request.body
        if (body.isPresent) {
            importService.import(toInputParams(body.get()))
        } else {
            logger.warn { "No input received!" }
        }
    }

    private fun toInputParams(input: String): ImportParams {
        return objectMapper.readValue(input, ImportParams::class.java)
    }
}