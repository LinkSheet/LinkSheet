package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DisableInAppBrowserInSelectedExportModel : ExportModel

@Serializable
@SerialName("DisableInAppBrowserInSelectedExportModelV1")
data class DisableInAppBrowserInSelectedExportModelV1(
    val id: Int,
    val packageName: String
) : DisableInAppBrowserInSelectedExportModel

fun DisableInAppBrowserInSelected.toExportModel(): DisableInAppBrowserInSelectedExportModel {
    return DisableInAppBrowserInSelectedExportModelV1(id, packageName)
}

fun DisableInAppBrowserInSelectedExportModel.fromExportModel(): DisableInAppBrowserInSelected {
    return when (this) {
        is DisableInAppBrowserInSelectedExportModelV1 -> DisableInAppBrowserInSelected(
            id = id,
            packageName = packageName,
        )
    }
}
