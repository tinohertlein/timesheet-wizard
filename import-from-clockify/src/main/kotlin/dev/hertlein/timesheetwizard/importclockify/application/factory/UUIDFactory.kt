package dev.hertlein.timesheetwizard.importclockify.application.factory

import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class UUIDFactory {

    fun create(): UUID = UUID.randomUUID()
}
