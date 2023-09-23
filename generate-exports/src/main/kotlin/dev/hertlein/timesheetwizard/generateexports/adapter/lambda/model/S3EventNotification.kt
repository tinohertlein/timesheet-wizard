package dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class S3EventNotification(
    val records: List<S3EventNotificationRecord?>? = null
)

@RegisterForReflection
data class S3EventNotificationRecord(val s3: S3Entity? = null)

@Suppress("ConstructorParameterNaming")
@RegisterForReflection
data class S3Entity(val `object`: S3ObjectEntity? = null)

@RegisterForReflection
data class S3ObjectEntity(val key: String? = null)
