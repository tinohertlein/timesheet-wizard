package dev.hertlein.timesheetwizard.spi.cloud

interface Repository {

    fun type(): String

    fun root(): String

    fun upload(key: String, content: ByteArray)

    fun download(key: String): ByteArray

    fun location(key: String) = "[${type()}]${root()}:$key"
}