package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import java.util.Properties

data class AwsClockifyConfig(
    override val reportsUrl: String,
    override val apiKey: String,
    override val workspaceId: String
) : ClockifyConfig {

    companion object {

        fun fromEnv(reportsUrl: String) = AwsClockifyConfig(
            reportsUrl,
            loadApiKey(),
            loadWorkspaceId()
        )

        fun fromPropertiesAndEnv() = AwsClockifyConfig(
            loadReportsUrl(PropertiesLoader.properties),
            loadApiKey(),
            loadWorkspaceId()
        )

        private fun loadReportsUrl(properties: Properties) = properties.getProperty("timesheet-wizard.import.clockify.reports-url", "")

        private fun loadApiKey() = System.getenv("CLOCKIFY_API_KEY")

        private fun loadWorkspaceId() = System.getenv("CLOCKIFY_WORKSPACE_ID")
    }
}