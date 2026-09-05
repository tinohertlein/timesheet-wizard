package dev.hertlein.timesheetwizard.app.scaleway

import dev.hertlein.timesheetwizard.core.anticorruption.Core
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ScalewayBeansFactory {

    @Bean
    @Profile("scaleway")
    fun minioClient(
        @Value("\${timesheet-wizard.scaleway.region}")
        region: String,
        @Value("\${timesheet-wizard.scaleway.access-key}")
        accessKey: String,
        @Value("\${timesheet-wizard.scaleway.secret-key}")
        secretKey: String
    ): MinioClient {
        return MinioClient.builder()
            .endpoint("https://s3.$region.scw.cloud")
            .credentials(accessKey, secretKey)
            .build()
    }

    @Bean
    fun objectMapper() = Core.objectMapper

    @Bean
    fun eventBus(repository: ScalewayObjectStorageRepository, clockifyConfig: ScalewayClockifyConfig) = Core.bootstrap(repository, clockifyConfig)
}