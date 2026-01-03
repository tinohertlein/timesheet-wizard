package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Joiner
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

internal class HttpReportClient(
    private val clockifyConfig: ClockifyConfig,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper
) {

    private val uri: URI = URI.create(Joiner.on("/").join(clockifyConfig.reportsUrl, "workspaces", clockifyConfig.workspaceId, "reports", "detailed"))

    fun fetchReport(requestBody: RequestBody): ResponseBody? {
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
            .header("X-Api-Key", clockifyConfig.apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return objectMapper.readValue(response.body(), ResponseBody::class.java)
    }
}