package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import java.util.Properties

data class AwsClockifyConfig(
    override val reportsUrl: String,
    override val apiKey: String,
    override val workspaceId: String
) : ClockifyConfig {

    companion object {

        fun fromPropertiesAndEnv() = PropertiesLoader.properties.let {
            AwsClockifyConfig(
                loadReportsUrl(it),
                loadApiKey(it),
                loadWorkspaceId(it)
            )
        }

        private fun loadReportsUrl(properties: Properties) = System.getenv("TIMESHEET_WIZARD_IMPORT_CLOCKIFY_REPORTS_URL")
            ?: properties.getProperty("timesheet-wizard.import.clockify.reports-url", "")

        private fun loadApiKey(properties: Properties) = System.getenv("TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY")
            ?: properties.getProperty("timesheet-wizard.import.clockify.api-key", "")

        private fun loadWorkspaceId(properties: Properties) = System.getenv("TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID")
            ?: properties.getProperty("timesheet-wizard.import.clockify.workspace-id", "")
    }
}