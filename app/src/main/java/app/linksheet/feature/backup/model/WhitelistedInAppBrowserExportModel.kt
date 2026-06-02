package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WhitelistedInAppBrowserExportModel : ExportModel

@Serializable
@SerialName("WhitelistedInAppBrowserExportModelV1")
data class WhitelistedInAppBrowserExportModelV1(
    val id: Int ,
    val packageName: String
) : WhitelistedInAppBrowserExportModel

fun WhitelistedInAppBrowser.toExportModel(): WhitelistedInAppBrowserExportModelV1 {
    return WhitelistedInAppBrowserExportModelV1(id, packageName)
}

fun WhitelistedInAppBrowserExportModel.fromExportModel(): WhitelistedInAppBrowser {
    return when(this) {
        is WhitelistedInAppBrowserExportModelV1 -> WhitelistedInAppBrowser(id, packageName)
    }
}
