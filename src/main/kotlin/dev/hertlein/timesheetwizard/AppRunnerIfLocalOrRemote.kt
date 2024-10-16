package dev.hertlein.timesheetwizard

import dev.hertlein.timesheetwizard.import_.core.ImportService
import dev.hertlein.timesheetwizard.import_.core.model.DateRangeType
import dev.hertlein.timesheetwizard.import_.core.model.ImportParams
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(SpringProfiles.LOCAL, SpringProfiles.REMOTE)
class AppRunnerIfLocalOrRemote(private val importService: ImportService) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val importParams = ImportParams(listOf("1000"), DateRangeType.CUSTOM_YEAR, "2022")
        importService.import(importParams)
    }
}