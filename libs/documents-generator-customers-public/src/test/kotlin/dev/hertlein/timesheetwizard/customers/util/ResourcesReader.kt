package dev.hertlein.timesheetwizard.customers.util

import com.google.common.io.Resources

object ResourcesReader {

    fun bytesFromResourceFile(resourceName: String): ByteArray =
        Resources.toByteArray(Resources.getResource(resourceName))
}
