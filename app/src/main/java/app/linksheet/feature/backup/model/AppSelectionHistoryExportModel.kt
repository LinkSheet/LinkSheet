package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppSelectionHistoryExportModel : ExportModel

@Serializable
@SerialName("AppSelectionHistoryExportModelV1")
data class AppSelectionHistoryExportModelV1(
    val id: Int,
    val host: String,
    val packageName: String,
    val lastUsed: Long,
) : AppSelectionHistoryExportModel

fun AppSelectionHistory.toExportModel(): AppSelectionHistoryExportModel {
    return AppSelectionHistoryExportModelV1(id, host, packageName, lastUsed)
}

fun AppSelectionHistoryExportModel.fromExportModel(): AppSelectionHistory {
    return when (this) {
        is AppSelectionHistoryExportModelV1 -> AppSelectionHistory(
            id = id,
            host = host,
            packageName = packageName,
            lastUsed = lastUsed
        )
    }
}
