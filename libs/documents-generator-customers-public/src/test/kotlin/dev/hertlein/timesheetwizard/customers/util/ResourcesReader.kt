package dev.hertlein.timesheetwizard.customers.util

import com.google.common.base.Charsets
import com.google.common.io.Resources
import java.io.InputStream

object ResourcesReader {

    fun bytesFromResourceFile(resourceName: String): ByteArray =
        Resources.toByteArray(Resources.getResource(resourceName))
}
