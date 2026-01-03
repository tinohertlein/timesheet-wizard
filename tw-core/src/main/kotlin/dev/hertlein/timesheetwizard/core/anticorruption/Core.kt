package dev.hertlein.timesheetwizard.core.anticorruption

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.ClockifyAdapter
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.HttpReportClient
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.RequestBodyFactory
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.ResponseBodyMapper
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.eventing.EventPublishAdapter
import dev.hertlein.timesheetwizard.core._import.domain.service.CustomerFactory
import dev.hertlein.timesheetwizard.core._import.domain.service.DateTimeFactory
import dev.hertlein.timesheetwizard.core._import.domain.service.ImportConfigLoader
import dev.hertlein.timesheetwizard.core._import.domain.service.ImportService
import dev.hertlein.timesheetwizard.core._import.domain.service.ImportServiceImpl
import dev.hertlein.timesheetwizard.core.export.adapter.incoming.eventing.EventSubscribeAdapter
import dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence.CloudPersistenceAdapter
import dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence.FilenameFactory
import dev.hertlein.timesheetwizard.core.export.domain.service.ExportService
import dev.hertlein.timesheetwizard.core.export.domain.service.config.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.export.domain.service.strategy.CsvV1
import dev.hertlein.timesheetwizard.core.export.domain.service.strategy.PdfV1
import dev.hertlein.timesheetwizard.core.export.domain.service.strategy.XlsxV1
import dev.hertlein.timesheetwizard.core.export.domain.service.strategy.XlsxV2
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import java.net.http.HttpClient

object Core {

    fun bootstrap(persistence: CloudPersistence, clockifyConfig: ClockifyConfig): ImportService {
        val objectMapper = ObjectMapper().apply {
            registerKotlinModule()
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        val eventBus = EventBus()

        val importService = bootstrapImportService(clockifyConfig, objectMapper, persistence, eventBus)
        val exportService = bootstrapExportService(persistence, objectMapper)

        EventMapper(eventBus)
        EventSubscribeAdapter(eventBus, exportService)

        return importService
    }

    private fun bootstrapImportService(
        clockifyConfig: ClockifyConfig,
        objectMapper: ObjectMapper,
        persistence: CloudPersistence,
        eventBus: EventBus
    ): ImportService {
        val httpReportClient = HttpReportClient(clockifyConfig, HttpClient.newHttpClient(), objectMapper)
        val clockifyIdsLoader = ClockifyIdsLoader(persistence, objectMapper)
        val clockifyAdapter = ClockifyAdapter(clockifyIdsLoader, httpReportClient, RequestBodyFactory(), ResponseBodyMapper())
        val importConfigLoader = ImportConfigLoader(persistence, objectMapper)
        val customerFactory = CustomerFactory(importConfigLoader)
        val eventPublishPort = EventPublishAdapter(eventBus)
        val importService = ImportServiceImpl(customerFactory, DateTimeFactory(), clockifyAdapter, eventPublishPort)
        return importService
    }

    private fun bootstrapExportService(
        persistence: CloudPersistence,
        objectMapper: ObjectMapper
    ): ExportService {
        val persistencePort = CloudPersistenceAdapter(persistence, FilenameFactory())
        val exportConfigLoader = ExportConfigLoader(persistence, objectMapper)
        val exportService = ExportService(exportConfigLoader, listOf(CsvV1(), PdfV1(), XlsxV1(), XlsxV2()), persistencePort)
        return exportService
    }
}