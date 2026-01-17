package dev.hertlein.timesheetwizard.app.gcp

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportingStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.inject.Named
import tools.jackson.databind.ObjectMapper

@Named("import")
@ApplicationScoped
class GcpFunctionAdapter : HttpFunction {

    @Inject
    private lateinit var eventBus: EventBus

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @ApplicationScoped
    fun eventBus(repository: GCPStorageRepository, clockifyConfig: GcpClockifyConfig) = Core.bootstrap(repository, clockifyConfig)

    @ApplicationScoped
    fun cloudFunctionJsonMapper() = Core.objectMapper

    override fun service(request: HttpRequest, response: HttpResponse) {
        eventBus.post(ImportingStartedEvent(objectMapper.readValue(request.reader, ImportParams::class.java)))
    }
}