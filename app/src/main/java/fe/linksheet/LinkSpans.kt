package fe.linksheet

import fe.android.span.helper.LinkTags

object LinkSpans {
    val map = mapOf(
        "fastforward-github" to "https://github.com/FastForwardTeam/FastForward",
        "clearurls-github" to "https://github.com/ClearURLs",
        "supabase-privacy" to "https://supabase.com/privacy",
        "aptabase" to "https://aptabase.com",
        "libredirect-github" to "https://github.com/libredirect/libredirect",
        "privacy-logs" to "https://github.com/LinkSheet/LinkSheet/blob/master/PRIVACY.md#logs",
        "privacy-exports" to "https://github.com/LinkSheet/LinkSheet/blob/master/PRIVACY.md#exports",
        "privacy-telemetry" to "https://github.com/LinkSheet/LinkSheet/blob/master/PRIVACY.md#telemetry"
    )

    val tags = LinkTags(urlIds = map)
}
