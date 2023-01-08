package dev.hertlein.timesheetwizard.importclockify.adapter.lambda.component

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Stopwatch
import dev.hertlein.timesheetwizard.importclockify.application.OrchestrationService
import dev.hertlein.timesheetwizard.importclockify.application.config.ImportConfig
import io.micronaut.function.aws.MicronautRequestHandler
import io.micronaut.http.HttpStatus
import jakarta.inject.Inject
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class FunctionRequestHandler : MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>() {

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var orchestrationService: OrchestrationService

    @Suppress("TooGenericExceptionCaught")
    override fun execute(input: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        val response = APIGatewayProxyResponseEvent()
        try {
            val stopwatch = Stopwatch.createStarted()
            val importConfig = objectMapper.readValue(input.body, ImportConfig::class.java)
            val persistenceResults = orchestrationService.execute(importConfig)

            val importResponse = ImportResponse(persistenceResults, stopwatch.stop().elapsed().seconds)
            response.statusCode = HttpStatus.OK.code
            response.body = objectMapper.writeValueAsString(importResponse)
            return response

        } catch (e: Exception) {
            logger.error { e }
            throw e
        }
    }
}
