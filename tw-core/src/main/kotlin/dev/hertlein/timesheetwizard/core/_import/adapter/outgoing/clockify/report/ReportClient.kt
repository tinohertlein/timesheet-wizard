package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

import com.google.common.net.HttpHeaders
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config.ClockifyConfig
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

@Component
internal class ReportClient(
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
            .contentType(APPLICATION_JSON)
            .body(requestBody)
            .header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE)
            .header("X-Api-Key", clockifyConfig.apiKey)
            .retrieve()
            .body(ResponseBody::class.java)
    }
}