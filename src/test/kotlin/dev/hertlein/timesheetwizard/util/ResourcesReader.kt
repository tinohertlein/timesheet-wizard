package dev.hertlein.timesheetwizard.util

import com.google.common.base.Charsets
import com.google.common.io.Resources

object ResourcesReader {

    fun bytesFromResourceFile(resourceName: String): ByteArray =
        Resources.toByteArray(Resources.getResource(resourceName))

    fun stringFromResourceFile(resourceName: String): String =
        Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8)

}
