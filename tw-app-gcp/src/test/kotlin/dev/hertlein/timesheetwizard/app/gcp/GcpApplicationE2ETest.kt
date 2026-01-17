package dev.hertlein.timesheetwizard.app.gcp

import com.google.auth.Credentials
import com.google.cloud.NoCredentials
import dev.hertlein.timesheetwizard.app.gcp.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import io.quarkus.google.cloud.functions.test.FunctionType
import io.quarkus.google.cloud.functions.test.WithFunction
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import jakarta.annotation.Priority
import jakarta.enterprise.inject.Alternative
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("GCP Application")
@QuarkusTest
@QuarkusTestResource(value = TestcontainersConfiguration::class, restrictToAnnotatedClass = false)
@TestProfile(GcpApplicationE2ETest::class)
@WithFunction(FunctionType.HTTP)
class GcpApplicationE2ETest : AbstractApplicationE2ETest(), QuarkusTestProfile {

    override fun getConfigOverrides() = mapOf(
        "timesheet-wizard.import.clockify.reports-url" to "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT",
        "timesheet-wizard.import.clockify.api-key" to "an-api-key",
        "timesheet-wizard.import.clockify.workspace-id" to "a-workspace-id",
        "timesheet-wizard.gcp.storage.bucket" to "tw-sheets",
        "quarkus.google.cloud.project-id" to "timesheet-wizard"
    )

    @Inject
    private lateinit var repository: GCPStorageRepository

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()
        repository.createBucketIfNotExists()
    }

    @Produces
    @Singleton
    @Alternative
    @Priority(1)
    fun gcpCredentials(): Credentials = NoCredentials.getInstance()

    @Test
    fun `should import and export timesheets to Google Cloud Storage`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        repository.upload(key, bytes)
    }

    private fun download(key: String): ByteArray {
        return repository.download(key)
    }

    private fun run() {
        given()
            .body("""{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""")
            .`when`()
            .post()
            .then()
            .statusCode(200)
    }
}