package dev.hertlein.timesheetwizard.app.gcp

import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import jakarta.inject.Singleton
import org.eclipse.microprofile.config.inject.ConfigProperty

@Singleton
data class GcpClockifyConfig(
    @param:ConfigProperty(name = "timesheet-wizard.import.clockify.reports-url")
    override val reportsUrl: String,
    @param:ConfigProperty(name = "timesheet-wizard.import.clockify.api-key")
    override val apiKey: String,
    @param:ConfigProperty(name = "timesheet-wizard.import.clockify.workspace-id")
    override val workspaceId: String
) : ClockifyConfig 