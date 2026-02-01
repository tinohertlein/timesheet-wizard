package dev.hertlein.timesheetwizard.app.local

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportingStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import tools.jackson.databind.ObjectMapper
import java.io.File

class LocalCliAdapter(
    private val repository: Repository? = null,
    private val clockifyReportsUrl: String = "https://reports.api.clockify.me/"
) : CliktCommand() {

    private val dataDir: File by argument(help = "The directory reading config files from and also writing generated reports to")
        .file(
            mustExist = true,
            canBeFile = false,
            mustBeReadable = true,
            mustBeWritable = true
        )

    private val eventFile: File by argument(help = "The file containing the import event")
        .file(
            mustExist = true,
            canBeDir = false,
            mustBeReadable = true
        )

    private val clockifyApiKey by option(envvar = "CLOCKIFY_API_KEY", help = "The Clockify API key to use for fetching timesheets")

    private val clockifyWorkspaceId by option(envvar = "CLOCKIFY_WORKSPACE_ID", help = "The Clockify Workspace Id to use for fetching timesheets")

    private val objectMapper: ObjectMapper = Core.objectMapper

    override fun run() {
        val eventBus = Core.bootstrap(
            repository ?: LocalRepository(dataDir),
            LocalClockifyConfig(
                reportsUrl = clockifyReportsUrl,
                apiKey = clockifyApiKey ?: throw IllegalStateException("No Clockify API key provided!"),
                workspaceId = clockifyWorkspaceId ?: throw IllegalStateException("No Clockify Workspace Id provided!")
            )
        )

        eventBus.post(ImportingStartedEvent(toInputParams(eventFile.readText())))
    }

    private fun toInputParams(input: String) = objectMapper.readValue(input, ImportParams::class.java)
}