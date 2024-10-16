package dev.hertlein.timesheetwizard.export.core

import dev.hertlein.timesheetwizard.export.core.port.PersistencePort
import dev.hertlein.timesheetwizard.export.core.strategy.ExportStrategy
import dev.hertlein.timesheetwizard.shared.configloader.ExportConfigLoader
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import lombok.SneakyThrows
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ExportService(
    private val exportConfigLoader: ExportConfigLoader,
    private val availableExportStrategies: List<ExportStrategy>,
    private val persistencePort: PersistencePort
) {

    @SneakyThrows
    fun export(timesheet: Timesheet) {
        val exportConfig = exportConfigLoader.loadExportConfig()
        val exportStrategyIdsForCustomer = exportConfig.findStrategiesFor(timesheet.customer)
        val applicableExportStrategiesForCustomer = findApplicableStrategies(exportStrategyIdsForCustomer)

        if (exportStrategyIdsForCustomer.isEmpty()) {
            logger.error { "No export strategy ids found for customer id ${timesheet.customer.id.value}." }

        } else if (applicableExportStrategiesForCustomer.isEmpty()) {
            logger.error { "No applicable export strategies found for customer id ${timesheet.customer.id.value}." }

        } else {
            applicableExportStrategiesForCustomer.forEach { strategy ->
                val timesheetDocument = strategy.create(exportConfig, timesheet)
                persistencePort.save(timesheetDocument)
            }
        }
    }

    private fun findApplicableStrategies(strategyIds: List<String>): List<ExportStrategy> {
        return availableExportStrategies.filter { it.type().name in strategyIds }
    }
}