package dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.component

import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.config.ClockifyConfig
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.model.ResponseBody
import mu.KotlinLogging
import org.springframework.http.HttpHeaders.ACCEPT
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

private val logger = KotlinLogging.logger {}


@Component
class ReportClient(
    private val clockifyConfig: ClockifyConfig,
    private val restClient: RestClient = RestClient.create()
) {

    private val uri = UriComponentsBuilder.fromUriString(clockifyConfig.reportsUrl)
        .path("workspaces/")
        .path(clockifyConfig.workspaceId)
        .path("/reports")
        .path("/detailed")
        .build().toUri()

    fun fetchReport(requestBody: RequestBody): ResponseBody? {
        return restClient
            .post()
            .uri(uri)
            .body(requestBody)
            .header(ACCEPT, "application/json")
            .header("X-Api-Key", clockifyConfig.apiKey)
            .retrieve()
            .body(ResponseBody::class.java)
    }
}
