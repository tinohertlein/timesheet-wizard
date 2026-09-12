package dev.hertlein.timesheetwizard.app.azure

import dev.hertlein.timesheetwizard.spi.ClockifyConfig
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("timesheet-wizard.import.clockify")
data class AzureClockifyConfig(
    override var reportsUrl: String,
    override var apiKey: String,
    override var workspaceId: String
) : ClockifyConfig