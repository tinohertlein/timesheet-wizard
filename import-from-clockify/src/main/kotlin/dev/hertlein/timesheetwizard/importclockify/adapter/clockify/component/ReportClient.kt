package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody
import io.micronaut.context.annotation.Context
import io.micronaut.http.HttpHeaders.ACCEPT
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.uri.UriBuilder
import jakarta.inject.Singleton
import reactor.core.publisher.Mono

@Singleton
@Context
class ReportClient(
    private val clockifyConfig: ClockifyConfig,
    private val httpClient: HttpClient
) {

    private val uri = UriBuilder.of(clockifyConfig.reportsUrl as CharSequence)
        .path("workspaces")
        .path(clockifyConfig.workspaceId)
        .path("reports")
        .path("detailed")
        .build()

    fun fetchReport(requestBody: RequestBody): ResponseBody =
        HttpRequest
            .POST(uri, requestBody)
            .header(ACCEPT, "application/json")
            .header("X-Api-Key", clockifyConfig.apiKey)
            .let { Mono.from(httpClient.retrieve(it, ResponseBody::class.java)) }
            .block()!!
}
