@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.backup.impl.core

import app.linksheet.feature.backup.api.ExportModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("v")
sealed interface BackupSchema

@Serializable
@SerialName("1")
data class BackupSchemaV1(
    val preferences: Map<String, Map<String, String>>? = null,
    val databaseItems: List<ExportModel>? = null
) : BackupSchema

@Serializable
data class LegacyBackupSchema(
    val preferences: List<LegacyBackupPreference>
) : BackupSchema
@Serializable
data class LegacyBackupPreference(
    val name: String,
    val value: String
)
