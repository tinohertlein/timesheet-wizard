package dev.hertlein.timesheetwizard.app.azure

import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("timesheet-wizard.import.clockify")
data class AzureClockifyConfig(
    override var reportsUrl: String,
    override var apiKey: String,
    override var workspaceId: String
) : ClockifyConfig