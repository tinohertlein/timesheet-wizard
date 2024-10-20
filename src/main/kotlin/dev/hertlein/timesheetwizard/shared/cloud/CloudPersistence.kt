package dev.hertlein.timesheetwizard.shared.cloud

interface CloudPersistence {

    fun root(): String

    fun upload(key: String, content: ByteArray)

    fun download(key: String): String
}