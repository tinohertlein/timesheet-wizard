package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.function.json.JacksonMapper
import org.springframework.cloud.function.json.JsonMapper
import org.springframework.context.annotation.Bean
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.SerializationFeature

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(AwsClockifyConfig::class)
class TwAwsApplication {

    @Bean
    fun cloudFunctionJsonMapper(builder: tools.jackson.databind.json.JsonMapper.Builder): JsonMapper {
        // TODO remove when https://github.com/spring-cloud/spring-cloud-function/issues/1319 is fixed
        val jsonMapper =
            builder
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, false)
                .build()
        return JacksonMapper(jsonMapper)
    }


    @Bean
    fun importService(repository: AwsS3Repository, clockifyConfig: AwsClockifyConfig): ImportService {
        return Core.bootstrap(repository, clockifyConfig)
    }
}


fun main(args: Array<String>) {
    runApplication<TwAwsApplication>(*args)
}