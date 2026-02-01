package dev.hertlein.timesheetwizard.app.local

import com.github.ajalt.clikt.testing.test
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetEnvironmentVariable
import java.nio.file.Files

@DisplayName("Local CLI Application")
@SetEnvironmentVariable(key = "CLOCKIFY_API_KEY", value = "an-api-key")
@SetEnvironmentVariable(key = "CLOCKIFY_WORKSPACE_ID", value = "a-workspace-id")
class LocalApplicationE2ETest : AbstractApplicationE2ETest() {

    private val dataDirectory = Files.createTempDirectory("data").toFile()
    private val eventFile = Files.createTempFile("event", ".json").toFile()
        .apply { writeText("""{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""") }

    private val repository = LocalRepository(dataDirectory)
    private val localCliAdapter = LocalCliAdapter(repository, "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT")

    @Test
    fun `should import and export timesheets to AWS S3`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        repository.upload(key, bytes)
    }

    private fun download(key: String): ByteArray {
        return repository.download(key)
    }

    private fun run() {
        val result = localCliAdapter.test("${dataDirectory.absolutePath} ${eventFile.absolutePath}", includeSystemEnvvars = true)

        assertThat(result.statusCode).withFailMessage { result.output }.isEqualTo(0)
    }
}