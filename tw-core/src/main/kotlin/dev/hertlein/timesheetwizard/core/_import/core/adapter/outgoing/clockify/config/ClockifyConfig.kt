package dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("timesheet-wizard.import.clockify")
internal data class ClockifyConfig(
    var reportsUrl: String,
    var apiKey: String,
    var workspaceId: String
)