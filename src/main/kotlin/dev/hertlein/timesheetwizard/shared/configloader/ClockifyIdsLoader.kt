package dev.hertlein.timesheetwizard.shared.configloader

interface ClockifyIdsLoader {

    fun loadClockifyIds(): Map<String, String>
}