package dev.hertlein.timesheetwizard.core.shared.configloader

import dev.hertlein.timesheetwizard.core.shared.model.ClockifyId

internal interface ClockifyIdsLoader {

    fun loadClockifyIds(): List<ClockifyId>
}