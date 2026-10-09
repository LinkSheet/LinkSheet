package app.linksheet.feature.backup.model

import app.linksheet.feature.backup.api.ExportModel
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Amp2HtmlMappingExportModel : ExportModel

@Serializable
@SerialName("Amp2HtmlMappingExportModelV1")
data class Amp2HtmlMappingExportModelV1(
    val ampUrl: String,
    val canonicalUrl: String?,
    val isCacheHit: Boolean
) : Amp2HtmlMappingExportModel

fun Amp2HtmlMapping.toExportModel(): Amp2HtmlMappingExportModel {
    return Amp2HtmlMappingExportModelV1(ampUrl, canonicalUrl, isCacheHit)
}

fun Amp2HtmlMappingExportModel.fromExportModel(): Amp2HtmlMapping {
    return when (this) {
        is Amp2HtmlMappingExportModelV1 -> Amp2HtmlMapping(ampUrl, canonicalUrl, isCacheHit)
    }
}
