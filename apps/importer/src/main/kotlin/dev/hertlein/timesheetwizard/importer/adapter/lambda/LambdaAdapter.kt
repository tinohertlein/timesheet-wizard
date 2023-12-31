package dev.hertlein.timesheetwizard.importer.adapter.lambda

import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dev.hertlein.timesheetwizard.importer.adapter.lambda.component.FunctionRequestHandler
import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime

class LambdaAdapter :
    AbstractMicronautLambdaRuntime<
            APIGatewayProxyRequestEvent,
            APIGatewayProxyResponseEvent,
            APIGatewayProxyRequestEvent,
            APIGatewayProxyResponseEvent>() {

    companion object {

        @JvmStatic
        fun main(vararg args: String) {
            LambdaAdapter().run(*args)
        }
    }

    override fun createRequestHandler(vararg args: String?):
            RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> =
        FunctionRequestHandler()
}
