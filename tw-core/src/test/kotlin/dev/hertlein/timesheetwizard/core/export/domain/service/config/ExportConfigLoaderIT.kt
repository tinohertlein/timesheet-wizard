package dev.hertlein.timesheetwizard.core.export.domain.service.config

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.TestApplication
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("ExportConfigLoader")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
internal class ExportConfigLoaderIT {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var exportConfigLoader: ExportConfigLoader

    @BeforeEach
    fun setup() {
        cloudPersistence.upload(
            "config/export.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/export.json")
        )
    }

    @Test
    fun `should load export config`() {
        val customerId = "1000"
        val exportConfig = exportConfigLoader.loadExportConfig(customerId)

        Assertions.assertThat(exportConfig).containsExactly(
            ExportConfig("CSV_V1", mapOf("login" to "rihe")),
            ExportConfig(
                "XLSX_V1", mapOf(
                    "contact-name" to "Richard Hendricks",
                    "contact-email" to "Richard.Hendricks@example.org"
                )
            ),
            ExportConfig(
                "PDF_V1", mapOf(
                    "contact-name" to "Richard Hendricks",
                    "contact-email" to "Richard.Hendricks@example.org"
                )
            ),
            ExportConfig("XLSX_V2", emptyMap()),
        )
    }
}