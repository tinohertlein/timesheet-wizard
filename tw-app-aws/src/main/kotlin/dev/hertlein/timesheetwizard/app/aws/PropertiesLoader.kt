package dev.hertlein.timesheetwizard.app.aws

import com.google.common.io.Resources
import java.util.Properties

object PropertiesLoader {

    val properties: Properties by lazy { load() }

    private fun load(): Properties {
        return Properties().apply {
            Resources.getResource("application.properties").openStream().use {
                load(it)
            }
        }
    }
}