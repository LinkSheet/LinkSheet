package app.linksheet.feature.backup.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

interface ExportModel {
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UnsupportedExportModel(val type: String?) : ExportModel
