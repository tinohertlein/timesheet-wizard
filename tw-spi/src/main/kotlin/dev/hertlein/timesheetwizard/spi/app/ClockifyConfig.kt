package dev.hertlein.timesheetwizard.spi.app

interface ClockifyConfig {
    val reportsUrl: String
    val apiKey: String
    val workspaceId: String
}