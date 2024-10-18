package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.ClockifyId

interface ClockifyIdsLoader {

    fun loadClockifyIds(): List<ClockifyId>
}