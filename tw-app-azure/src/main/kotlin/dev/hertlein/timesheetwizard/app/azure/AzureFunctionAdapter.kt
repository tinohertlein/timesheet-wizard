package dev.hertlein.timesheetwizard.app.azure

import com.google.common.eventbus.EventBus
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.microsoft.azure.functions.annotation.TimerTrigger
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import mu.KotlinLogging
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.util.Optional

private val logger = KotlinLogging.logger {}

@Component
class AzureFunctionAdapter(
    private val objectMapper: ObjectMapper,
    private val eventBus: EventBus,
) {

    @FunctionName("import")
    fun import(
        @HttpTrigger(name = "import", methods = [HttpMethod.POST], authLevel = AuthorizationLevel.FUNCTION)
        request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext?
    ) {
        doImport(request.body)
    }

    @FunctionName("importDaily")
    fun importDaily(
        @TimerTrigger(name = "importDailyTrigger", schedule = "0 30 17 * * 1-5")
        timerInfo: String,
        context: ExecutionContext
    ) {
        doImport(Optional.of("""{"customerIds": [], "dateRangeType": "THIS_MONTH"}"""))
    }

    @FunctionName("importMonthly")
    fun importMonthly(
        @TimerTrigger(name = "importMonthlyTrigger", schedule = "0 0 5 1 * *")
        timerInfo: String,
        context: ExecutionContext
    ) {
        doImport(Optional.of("""{"customerIds": [], "dateRangeType": "LAST_MONTH"}"""))
    }

    private fun doImport(body: Optional<String>) {
        if (body.isPresent) {
            eventBus.post(toInputParams(body.get()))
        } else {
            logger.warn { "No input received!" }
        }
    }

    private fun toInputParams(input: String): ImportParams {
        return objectMapper.readValue(input, ImportParams::class.java)
    }
}