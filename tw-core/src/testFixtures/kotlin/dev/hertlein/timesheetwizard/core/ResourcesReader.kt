package dev.hertlein.timesheetwizard.core

import com.google.common.io.Resources
import java.nio.charset.StandardCharsets

object ResourcesReader {

    fun bytesFromResourceFile(resourceName: String): ByteArray =
        Resources.toByteArray(Resources.getResource(resourceName))

    fun stringFromResourceFile(resourceName: String): String =
        Resources.toString(Resources.getResource(resourceName), StandardCharsets.UTF_8)
}
