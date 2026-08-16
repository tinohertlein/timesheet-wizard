package dev.hertlein.timesheetwizard.spi

interface ClockifyConfig {
    val reportsUrl: String
    val apiKey: String
    val workspaceId: String
}