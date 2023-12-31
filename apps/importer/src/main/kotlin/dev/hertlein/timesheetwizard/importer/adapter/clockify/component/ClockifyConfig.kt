package dev.hertlein.timesheetwizard.importer.adapter.clockify.component

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("clockify")
data class ClockifyConfig(
    var reportsUrl: String?,
    var apiKey: String?,
    var workspaceId: String?
)
