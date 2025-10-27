package app.linksheet.feature.engine.core.fetcher.preview

import androidx.room.TypeConverter
import app.linksheet.feature.engine.core.fetcher.FetchResult
import fe.std.result.IResult
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

interface PreviewSource {
    suspend fun fetch(urlString: String): IResult<PreviewFetchResult>
    suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewFetchResult>
}

enum class PreviewFetchResultId {
    None,
    NonHtml,
    Simple,
    Rich;

    fun hasPreview(): Boolean {
        return this == Simple || this == Rich
    }

    companion object Converter {
        @TypeConverter
        fun toInt(id: PreviewFetchResultId): Int {
            return id.ordinal
        }

        @TypeConverter
        fun toPreviewFetchResultId(ordinal: Int): PreviewFetchResultId {
            return entries[ordinal]
        }
    }
}

sealed interface PreviewFetchResult : FetchResult {
    val id: PreviewFetchResultId
    val url: String

    data class NoPreview(override val url: String) : PreviewFetchResult {
        override val id = PreviewFetchResultId.None
    }

    data class NonHtmlPage(override val url: String) : PreviewFetchResult {
        override val id = PreviewFetchResultId.NonHtml
    }
}

sealed interface HtmlPreviewResult : PreviewFetchResult {
    val htmlText: String

    data class Rich(
        override val url: String,
        override val htmlText: String,
        val title: String?,
        val description: String?,
        val favicon: String?,
        val thumbnail: String?,
    ) : HtmlPreviewResult {
        override val id = PreviewFetchResultId.Rich
    }

    data class Simple(
        override val url: String,
        override val htmlText: String,
        val title: String?,
        val favicon: String?
    ) : HtmlPreviewResult {
        override val id = PreviewFetchResultId.Simple
    }
}

// TODO: This should eventually be removed once the Link engine is stable and the old resolver implementation has been removed
fun PreviewFetchResult.toUnfurlResult(): UnfurlResult {
    return when (this) {
        is HtmlPreviewResult.Rich -> UnfurlResult(
            url = url.toHttpUrl(),
            title = title,
            description = description,
            favicon = favicon?.toHttpUrlOrNull(),
            thumbnail = thumbnail?.toHttpUrlOrNull()
        )

        is HtmlPreviewResult.Simple -> UnfurlResult(
            url = url.toHttpUrl(),
            title = title,
            description = null,
            favicon = favicon?.toHttpUrlOrNull(),
            thumbnail = null
        )

        is PreviewFetchResult.NoPreview -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )

        is PreviewFetchResult.NonHtmlPage -> UnfurlResult(
            url = url.toHttpUrl(),
            title = null,
            description = null,
            favicon = null,
            thumbnail = null
        )
    }
}
