package dev.hertlein.timesheetwizard.spi.cloud

interface CloudPersistence {

    fun type(): String

    fun root(): String

    fun upload(key: String, content: ByteArray)

    fun download(key: String): ByteArray
}