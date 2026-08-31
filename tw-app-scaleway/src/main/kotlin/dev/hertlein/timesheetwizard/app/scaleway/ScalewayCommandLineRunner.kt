package dev.hertlein.timesheetwizard.app.scaleway

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import kotlin.system.exitProcess


private val logger = KotlinLogging.logger {}

@Component
class ScalewayCommandLineRunner(
    private val eventBus: EventBus,
    private val objectMapper: ObjectMapper,
    private val environment: Environment
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (args.isEmpty()) {
            eventBus.post(ImportStartedEvent(toInputParams("""{"customerIds": ["1101"], "dateRangeType": "THIS_WEEK"}""")))
        } else {
            logger.info { "Importing timesheets with args: ${args.first()}" }
            eventBus.post(ImportStartedEvent(toInputParams(args.first())))
        }
        if (environment.activeProfiles.contains("scaleway")) {
            exitProcess(0)
        }
    }

    private fun toInputParams(input: String) = objectMapper.readValue(input, ImportParams::class.java)
}