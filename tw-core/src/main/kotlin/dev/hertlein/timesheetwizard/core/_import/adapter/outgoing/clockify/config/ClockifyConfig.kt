package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("timesheet-wizard.import.clockify")
data class ClockifyConfig(
    var reportsUrl: String,
    var apiKey: String,
    var workspaceId: String
)