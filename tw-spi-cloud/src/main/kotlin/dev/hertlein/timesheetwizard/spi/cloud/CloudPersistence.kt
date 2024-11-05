package dev.hertlein.timesheetwizard.spi.cloud

interface CloudPersistence {

    fun root(): String

    fun upload(key: String, content: ByteArray)

    fun download(key: String): ByteArray
}