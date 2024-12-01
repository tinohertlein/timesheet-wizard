package dev.hertlein.timesheetwizard.core.export.domain.service

import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.port.PersistencePort
import dev.hertlein.timesheetwizard.core.export.domain.service.config.ExportConfig
import dev.hertlein.timesheetwizard.core.export.domain.service.config.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.export.domain.service.strategy.ExportStrategy
import lombok.SneakyThrows
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
internal class ExportService(
    private val exportConfigLoader: ExportConfigLoader,
    private val availableExportStrategies: List<ExportStrategy>,
    private val persistencePort: PersistencePort
) {

    @SneakyThrows
    fun export(timesheet: ExportTimesheet) {
        val exportStrategiesForCustomer = exportConfigLoader.loadExportConfig(timesheet.customer.id)
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
                persistencePort.save(timesheetDocument)
            }
        }
    }

    private fun findApplicableStrategies(configs: List<ExportConfig>): List<Pair<ExportStrategy, ExportConfig>> {
        return configs.map { config -> Pair(availableExportStrategies.first { it.type().name == config.type }, config) }
    }
}