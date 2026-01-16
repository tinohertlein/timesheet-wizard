package dev.hertlein.timesheetwizard.core.exporting.domain.service

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import dev.hertlein.timesheetwizard.core.exporting.domain.service.config.ExportConfig
import dev.hertlein.timesheetwizard.core.exporting.domain.service.config.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy.ExportStrategy
import lombok.SneakyThrows
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class ExportService(
    private val exportConfigLoader: ExportConfigLoader,
    private val availableExportStrategies: List<ExportStrategy>,
    private val repositoryPort: RepositoryPort
) {

    @SneakyThrows
    fun export(timesheet: ExportTimesheet) {
        logger.info { "Exporting timesheet for customer '${timesheet.customer.id}' and date range ${timesheet.dateRange}..." }

        val exportStrategiesForCustomer = exportConfigLoader.loadExportConfigFor(timesheet.customer.id)
        val applicableExportStrategiesForCustomer = findApplicableStrategies(exportStrategiesForCustomer)

        if (exportStrategiesForCustomer.isEmpty()) {
            logger.error { "No export strategy ids found for customer id ${timesheet.customer.id}." }

        } else if (applicableExportStrategiesForCustomer.isEmpty()) {
            logger.error { "No applicable export strategies found for customer id ${timesheet.customer.id}." }

        } else if (timesheet.isEmpty()) {
            logger.info { "Timesheet for customer '${timesheet.customer.id}' and date range ${timesheet.dateRange} is empty." }

        } else {
            applicableExportStrategiesForCustomer.forEach { strategy ->
                val timesheetDocument = strategy.first.create(strategy.second.params, timesheet)
                repositoryPort.save(timesheetDocument)
            }
            logger.info {
                "Exported timesheet for customer '${timesheet.customer.id}' and date range ${timesheet.dateRange}."
            }
        }
    }

    private fun findApplicableStrategies(configs: List<ExportConfig>): List<Pair<ExportStrategy, ExportConfig>> {
        return configs.map { config -> Pair(availableExportStrategies.first { it.type().name == config.type }, config) }
    }
}