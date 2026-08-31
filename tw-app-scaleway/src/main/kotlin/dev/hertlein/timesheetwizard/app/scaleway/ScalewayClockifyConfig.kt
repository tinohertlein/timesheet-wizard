package dev.hertlein.timesheetwizard.app.scaleway

import dev.hertlein.timesheetwizard.spi.ClockifyConfig
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("timesheet-wizard.import.clockify")
data class ScalewayClockifyConfig(
    override var reportsUrl: String,
    override var apiKey: String,
    override var workspaceId: String
) : ClockifyConfig