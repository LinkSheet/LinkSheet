package fe.linksheet.util

import fe.android.span.helper.LinkTags

object LinkConstants {
    val tags = LinkTags(
        urlIds = mapOf(
            "fastforward-github" to FastForwardRepo,
            "clearurls-github" to ClearURLsOrg,
            "supabase-privacy" to "https://supabase.com/privacy",
            "aptabase" to "https://aptabase.com",
            "libredirect-github" to LibRedirectRepo,
            "privacy-logs" to "${LinkSheet.Wiki}/Privacy#logs",
            "privacy-exports" to "${LinkSheet.Wiki}/Privacy#exports",
            "privacy-telemetry" to "${LinkSheet.Wiki}/Privacy#telemetry",
            "privacy-amp2html" to "${LinkSheet.Wiki}/Privacy#amp2html",
            "privacy-follow-redirects" to "${LinkSheet.Wiki}/Privacy#follow-redirects",
            "privacy-downloader" to "${LinkSheet.Wiki}/Privacy#downloader",
            "device-issues-xiaomi" to LinkSheet.WikiDeviceIssuesXiaomi
        )
    )
}

fun Github(user: String, repo: String? = null): String {
    val repoStr = repo?.let { "/$it" } ?: ""
    return "https://github.com/$user$repoStr"
}


object LinkSheet {
    val Org = Github("LinkSheet")
    val Repo = "$Org/LinkSheet"
    val Wiki = "$Repo/wiki"
    val WikiDeviceIssuesXiaomi = "${Wiki}/Device%E2%80%90specific-issues#xiaomimiui"

    val CompatReleases = "$Org/compat/releases"
}


val OpenLinkWithRepo = Github("tasomaniac", "OpenLinkWith")
val MastodonRedirectRepo = Github("zacharee", "MastodonRedirect")
val SealRepo = Github("JunkFood02", "Seal")
val GmsFlagsRepo = Github("polodarb", "GMS-Flags")
val LibRedirectRepo = Github("libredirect", "libredirect")
val ClearURLsOrg = Github("ClearURLs")
val FastForwardRepo = Github("FastForwardTeam", "FastForward")

const val ShizukuDownload = "https://shizuku.rikka.app/download"
