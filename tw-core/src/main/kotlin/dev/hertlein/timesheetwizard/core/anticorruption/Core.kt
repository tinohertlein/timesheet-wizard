package dev.hertlein.timesheetwizard.core.anticorruption

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository.FilenameFactory
import dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository.RepositoryAdapter
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
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.net.http.HttpClient
import dev.hertlein.timesheetwizard.core.exporting.adapter.incoming.eventing.EventConsumeAdapter as ExportingEventConsumeAdapter
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.EventConsumeAdapter as ImportingEventConsumeAdapter

object Core {

    val objectMapper: JsonMapper = JsonMapper.builder()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, false)
        .addModule(kotlinModule())
        .build()

    fun bootstrap(repository: Repository, clockifyConfig: ClockifyConfig): EventBus {
        val eventBus = EventBus()

        val importService = bootstrapImportService(clockifyConfig, repository, eventBus)
        val exportService = bootstrapExportService(repository)

        EventMapper(eventBus)
        ImportingEventConsumeAdapter(eventBus, importService)
        ExportingEventConsumeAdapter(eventBus, exportService)

        return eventBus
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
        val eventPublishAdapter = EventPublishAdapter(eventBus)
        val importService = ImportService(customerFactory, DateTimeFactory(), clockifyAdapter, eventPublishAdapter)
        return importService
    }

    private fun bootstrapExportService(
        repository: Repository
    ): ExportService {
        val repositoryAdapter = RepositoryAdapter(repository, FilenameFactory())
        val exportConfigLoader = ExportConfigLoader(repository, objectMapper)
        val exportService = ExportService(exportConfigLoader, listOf(CsvV1(), PdfV1(), XlsxV1(), XlsxV2()), repositoryAdapter)
        return exportService
    }
}