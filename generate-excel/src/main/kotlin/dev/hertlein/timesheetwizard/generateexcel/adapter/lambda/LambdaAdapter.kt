package dev.hertlein.timesheetwizard.generateexcel.adapter.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.common.base.Stopwatch
import dev.hertlein.timesheetwizard.generateexcel.adapter.lambda.model.GenerationResponse
import dev.hertlein.timesheetwizard.generateexcel.adapter.lambda.model.S3EventNotification
import dev.hertlein.timesheetwizard.generateexcel.application.OrchestrationService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class LambdaAdapter(private val orchestrationService: OrchestrationService) :
    RequestHandler<S3EventNotification, GenerationResponse> {

    override fun handleRequest(s3EventNotification: S3EventNotification, context: Context): GenerationResponse {
        logger.debug { "s3EventNotification: $s3EventNotification" }
        val timesheetLocation = s3EventNotification.records?.get(0)?.s3?.`object`?.key
        requireNotNull(timesheetLocation) { "Please provide the location of the timesheet." }
        val stopwatch = Stopwatch.createStarted()

        val persistenceResult = orchestrationService.execute(timesheetLocation)

        return GenerationResponse(persistenceResult, stopwatch.stop().elapsed().seconds)
    }
}

