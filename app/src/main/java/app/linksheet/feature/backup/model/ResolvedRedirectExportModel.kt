package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ResolvedRedirectExportModel : ExportModel

@Serializable
@SerialName("ResolvedRedirectExportModelV1")
data class ResolvedRedirectExportModelV1(
    val shortUrl: String,
    val resolvedUrl: String?
) : ResolvedRedirectExportModel

fun ResolvedRedirect.toExportModel(): ResolvedRedirectExportModel {
    return ResolvedRedirectExportModelV1(shortUrl, resolvedUrl)
}

fun ResolvedRedirectExportModel.fromExportModel(): ResolvedRedirect {
    return when(this) {
        is ResolvedRedirectExportModelV1 -> ResolvedRedirect(shortUrl, resolvedUrl)
    }
}
