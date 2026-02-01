package dev.hertlein.timesheetwizard.app.local

import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig

data class LocalClockifyConfig(
    override val reportsUrl: String,
    override val apiKey: String,
    override val workspaceId: String
) : ClockifyConfig 