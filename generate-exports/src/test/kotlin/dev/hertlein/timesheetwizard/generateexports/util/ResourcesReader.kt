package dev.hertlein.timesheetwizard.generateexports.util

import com.google.common.base.Charsets
import com.google.common.io.Resources

object ResourcesReader {
    fun stringFromResourceFile(resourceName: String): String =
        Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8)

    fun bytesFromResourceFile(resourceName: String): ByteArray =
        Resources.toByteArray(Resources.getResource(resourceName))
}
