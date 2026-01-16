package dev.hertlein.timesheetwizard.app.aws

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportingStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import tools.jackson.databind.ObjectMapper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream


class AwsLambdaAdapter(
    clockifyConfig: ClockifyConfig = AwsClockifyConfig.fromPropertiesAndEnv(),
    repository: AwsS3Repository = AwsS3Repository.fromProperties(),
    private val eventBus: EventBus = Core.bootstrap(repository, clockifyConfig),
) : RequestStreamHandler {

    private val objectMapper: ObjectMapper = Core.objectMapper

    override fun handleRequest(input: InputStream, output: OutputStream?, context: Context?) {
        BufferedReader(InputStreamReader(input, Charsets.US_ASCII)).use {
            eventBus.post(ImportingStartedEvent(toInputParams(it.readText())))
        }
    }

    private fun toInputParams(input: String) = objectMapper.readValue(input, ImportParams::class.java)
}