package app.linksheet.lib.github.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ReleaseAsset(
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    @SerialName("content_type") val contentType: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("digest") val digest: String?,
    @SerialName("download_count") val downloadCount: Long,
    @SerialName("id") val id: Long,
    @SerialName("label") val label: String?,
    @SerialName("name") val name: String,
    @SerialName("node_id") val nodeId: String,
    @SerialName("size") val size: Long,
    @SerialName("state") val state: AssetState,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("uploader") val uploader: Author? = null,
    @SerialName("url") val url: String,
)

@Serializable
enum class AssetState {
    @SerialName("uploaded")
    UPLOADED,
    @SerialName("open")
    OPEN
}
