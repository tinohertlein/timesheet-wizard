package dev.hertlein.timesheetwizard.core.anticorruption

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.persistence.CloudPersistenceAdapter
import dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.persistence.FilenameFactory
import dev.hertlein.timesheetwizard.core.exporting.domain.service.ExportService
import dev.hertlein.timesheetwizard.core.exporting.domain.service.config.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy.CsvV1
import dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy.PdfV1
import dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy.XlsxV1
import dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy.XlsxV2
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.ClockifyAdapter
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.HttpReportClient
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.RequestBodyFactory
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.ResponseBodyMapper
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.eventing.EventPublishAdapter
import dev.hertlein.timesheetwizard.core.importing.domain.service.CustomerFactory
import dev.hertlein.timesheetwizard.core.importing.domain.service.DateTimeFactory
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportConfigLoader
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import java.net.http.HttpClient
import dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing.EventConsumeAdapter as ExportingEventConsumeAdapter
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.EventConsumeAdapter as ImportingEventConsumeAdapter

object Core {

    fun bootstrap(repository: Repository, clockifyConfig: ClockifyConfig): EventBus {
        val objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        val eventBus = EventBus()

        val importService = bootstrapImportService(clockifyConfig, objectMapper, repository, eventBus)
        val exportService = bootstrapExportService(repository, objectMapper)

        EventMapper(eventBus)
        ImportingEventConsumeAdapter(eventBus, importService)
        ExportingEventConsumeAdapter(eventBus, exportService)

        return eventBus
    }

    private fun bootstrapImportService(
        clockifyConfig: ClockifyConfig,
        objectMapper: ObjectMapper,
        repository: Repository,
        eventBus: EventBus
    ): ImportService {
        val httpReportClient = HttpReportClient(clockifyConfig, HttpClient.newHttpClient(), objectMapper)
        val clockifyIdsLoader = ClockifyIdsLoader(repository, objectMapper)
        val clockifyAdapter = ClockifyAdapter(clockifyIdsLoader, httpReportClient, RequestBodyFactory(), ResponseBodyMapper())
        val importConfigLoader = ImportConfigLoader(repository, objectMapper)
        val customerFactory = CustomerFactory(importConfigLoader)
        val eventPublishAdapter = EventPublishAdapter(eventBus)
        val importService = ImportService(customerFactory, DateTimeFactory(), clockifyAdapter, eventPublishAdapter)
        return importService
    }

    private fun bootstrapExportService(
        repository: Repository,
        objectMapper: ObjectMapper
    ): ExportService {
        val persistencePort = CloudPersistenceAdapter(repository, FilenameFactory())
        val exportConfigLoader = ExportConfigLoader(repository, objectMapper)
        val exportService = ExportService(exportConfigLoader, listOf(CsvV1(), PdfV1(), XlsxV1(), XlsxV2()), persistencePort)
        return exportService
    }
}