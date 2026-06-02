package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface PreferredAppExportModel : ExportModel

@Serializable
@SerialName("PreferredAppExportModelV1")
data class PreferredAppExportModelV1(
    val id: Int,
    val host: String,
    val packageName: String?,
    val component: String?,
    val alwaysPreferred: Boolean,
) : PreferredAppExportModel

fun PreferredApp.toExportModel(): PreferredAppExportModel {
    return PreferredAppExportModelV1(id, host, _packageName, _component, alwaysPreferred)
}

fun PreferredAppExportModel.fromExportModel(): PreferredApp {
    return when (this) {
        is PreferredAppExportModelV1 -> PreferredApp(
            id = id,
            host = host,
            _packageName = packageName,
            _component = component,
            alwaysPreferred = alwaysPreferred
        )
    }
}
