package dev.hertlein.timesheetwizard.core.export.core.service

import dev.hertlein.timesheetwizard.core.export.core.port.PersistencePort
import dev.hertlein.timesheetwizard.core.export.core.service.config.ExportConfig
import dev.hertlein.timesheetwizard.core.export.core.service.config.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.export.core.strategy.ExportStrategy
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
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
    fun export(timesheet: Timesheet) {
        val exportStrategiesForCustomer = exportConfigLoader.loadExportConfig(timesheet.customer)
        val applicableExportStrategiesForCustomer = findApplicableStrategies(exportStrategiesForCustomer)

        if (exportStrategiesForCustomer.isEmpty()) {
            logger.error { "No export strategy ids found for customer id ${timesheet.customer.id.value}." }

        } else if (applicableExportStrategiesForCustomer.isEmpty()) {
            logger.error { "No applicable export strategies found for customer id ${timesheet.customer.id.value}." }

        } else if (timesheet.isEmpty()) {
            logger.info { "Timesheet for customer '${timesheet.customer.id.value}' and date range ${timesheet.dateRange} is empty." }

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