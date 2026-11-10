package app.linksheet.lib.github.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    @SerialName("assets") val assets: List<ReleaseAsset> = emptyList(),
    @SerialName("assets_url") val assetsUrl: String,
    @SerialName("author") val author: Author,
    @SerialName("body") val body: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("discussion_url") val discussionUrl: String? = null,
    @SerialName("draft") val draft: Boolean,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("id") val id: Int,
    @SerialName("immutable") val immutable: Boolean,
    @SerialName("name") val name: String?,
    @SerialName("node_id") val nodeId: String,
    @SerialName("prerelease") val prerelease: Boolean,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("tag_name") val tagName: String,
    @SerialName("tarball_url") val tarballUrl: String? = null,
    @SerialName("target_commitish") val targetCommitish: String,
    @SerialName("upload_url") val uploadUrl: String,
    @SerialName("url") val url: String,
    @SerialName("zipball_url") val zipballUrl: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("body_html") val bodyHtml: String? = null,
    @SerialName("body_text") val bodyText: String? = null,
    @SerialName("mentions_count") val mentionsCount: Int? = null,
    @SerialName("reactions") val reactions: ReactionRollup? = null
)


@Serializable
data class ReactionRollup(
    @SerialName("url") val url: String,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("+1") val plusOne: Int,
    @SerialName("-1") val minusOne: Int,
    @SerialName("laugh") val laugh: Int,
    @SerialName("confused") val confused: Int,
    @SerialName("heart") val heart: Int,
    @SerialName("hooray") val hooray: Int,
    @SerialName("eyes") val eyes: Int,
    @SerialName("rocket") val rocket: Int
)
