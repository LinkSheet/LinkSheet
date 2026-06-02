package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WhitelistedNormalBrowserExportModel : ExportModel

@Serializable
@SerialName("WhitelistedNormalBrowserExportModelV1")
data class WhitelistedNormalBrowserExportModelV1(
    val id: Int,
    val packageName: String
) : WhitelistedNormalBrowserExportModel

fun WhitelistedNormalBrowser.toExportModel(): WhitelistedNormalBrowserExportModel {
    return WhitelistedNormalBrowserExportModelV1(id, packageName)
}

fun WhitelistedNormalBrowserExportModel.fromExportModel(): WhitelistedNormalBrowser {
    return when (this) {
        is WhitelistedNormalBrowserExportModelV1 -> WhitelistedNormalBrowser(id, packageName)
    }
}
