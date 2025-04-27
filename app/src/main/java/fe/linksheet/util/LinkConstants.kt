package fe.linksheet.util

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import fe.android.span.helper.LinkTags
import fe.linksheet.R

@Immutable
class LinkSheetLinkTags(
    val urlAnnotationKey: String = "url",
    val urlIdAnnotationKey: String = "url-id",
    private val urlIds: LinkAssets
) : LinkTags {
    private val deprecated = mapOf(
        "fastforward-github" to "github.repository.fastforward",
        "clearurls-github" to "github.org.clearurls",
        "supabase-privacy" to "web.supabase.privacy",
        "aptabase" to "web.aptabase",
        "libredirect-github" to "github.repository.libredirect",
        "privacy-logs" to "github.linksheet.wiki.privacy.logs",
        "privacy-exports" to "github.linksheet.wiki.privacy.exports",
        "privacy-telemetry" to "github.linksheet.wiki.privacy.telemetry",
        "privacy-amp2html" to "github.linksheet.wiki.privacy.amp2html",
        "privacy-follow-redirects" to "github.linksheet.wiki.privacy.follow-redirects",
        "privacy-downloader" to "github.linksheet.wiki.privacy.downloader",
        "device-issues-xiaomi" to "github.linksheet.wiki.device-issues.xiaomi"
    )

    override fun get(key: String, value: String): String? {
        Log.d("LinkSheetLinkTags", "Looking up $key, $value")
        val link = when (key) {
            urlAnnotationKey -> value
            urlIdAnnotationKey -> getById(value)
            else -> return null
        }

        return link
    }

    override fun getById(id: String): String? {
        val newId = deprecated[id]
        if (newId != null) return getById(newId)
        return urlIds[id]
    }
}

typealias LinkAssets = Map<String, String>

fun Github(user: String, repo: String? = null): String {
    val repoStr = repo?.let { "/$it" } ?: ""
    return "https://github.com/$user$repoStr"
}

object LinkSheet {
    val Org = Github("LinkSheet")
    val Repo = "$Org/LinkSheet"
    val Wiki = "$Repo/wiki"
    val WikiDeviceIssuesXiaomi = "${Wiki}/Device%E2%80%90specific-issues#xiaomimiui"

    val WikiDeviceIssuesXiaomi2 = WikiPage(
        "LinkSheet",
        "LinkSheet",
        "Device‐specific-issues",
        id = "xiaomimiui"
    )

    val WikiExperiments = WikiPage(
        "LinkSheet",
        "LinkSheet",
        "Changelog-(Experiments)",
        customTitle = R.string.settings_main_experiment_changelog__title_changelog
    )

    val CompatReleases = "$Org/compat/releases"
}

class WikiPage(
    val org: String,
    val repo: String,
    val page: String,
    val id: String? = null,
    @param:StringRes val customTitle: Int? = null
) {
    val url = "https://github.com/$org/$repo/wiki/$page"
    val rawUrl = "https://raw.githubusercontent.com/wiki/$org/$repo/$page.md"
}


const val ShizukuDownload = "https://shizuku.rikka.app/download"
