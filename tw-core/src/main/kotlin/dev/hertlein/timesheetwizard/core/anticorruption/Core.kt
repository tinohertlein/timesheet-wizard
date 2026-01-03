package dev.hertlein.timesheetwizard.core.anticorruption

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing.EventSubscribeAdapter
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
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportServiceImpl
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.net.http.HttpClient

object Core {

    val objectMapper: JsonMapper = JsonMapper.builder()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, false)
        .addModule(kotlinModule())
        .build()

    fun bootstrap(repository: Repository, clockifyConfig: ClockifyConfig): ImportService {
        val eventBus = EventBus()

        val importService = bootstrapImportService(clockifyConfig, repository, eventBus)
        val exportService = bootstrapExportService(repository)

        EventMapper(eventBus)
        EventSubscribeAdapter(eventBus, exportService)

        return importService
    }

    private fun bootstrapImportService(
        clockifyConfig: ClockifyConfig,
        repository: Repository,
        eventBus: EventBus
    ): ImportService {
        val httpReportClient = HttpReportClient(clockifyConfig, HttpClient.newHttpClient(), objectMapper)
        val clockifyIdsLoader = ClockifyIdsLoader(repository, objectMapper)
        val clockifyAdapter = ClockifyAdapter(clockifyIdsLoader, httpReportClient, RequestBodyFactory(), ResponseBodyMapper())
        val importConfigLoader = ImportConfigLoader(repository, objectMapper)
        val customerFactory = CustomerFactory(importConfigLoader)
        val eventPublishPort = EventPublishAdapter(eventBus)
        val importService = ImportServiceImpl(customerFactory, DateTimeFactory(), clockifyAdapter, eventPublishPort)
        return importService
    }

    private fun bootstrapExportService(
        repository: Repository
    ): ExportService {
        val persistencePort = CloudPersistenceAdapter(repository, FilenameFactory())
        val exportConfigLoader = ExportConfigLoader(repository, objectMapper)
        val exportService = ExportService(exportConfigLoader, listOf(CsvV1(), PdfV1(), XlsxV1(), XlsxV2()), persistencePort)
        return exportService
    }
}