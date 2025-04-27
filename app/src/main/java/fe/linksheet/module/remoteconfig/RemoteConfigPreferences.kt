package fe.linksheet.module.remoteconfig

import fe.android.preference.helper.PreferenceDefinition
import fe.linksheet.module.preference.jsonMapped
import fe.linksheet.util.LinkAssets

object RemoteConfigPreferences : PreferenceDefinition() {
    val linkAssets = jsonMapped<LinkAssets>(
        "link_assets", mapOf(
            "github.linksheet.wiki.privacy.logs" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#logs",
            "github.linksheet.wiki.privacy.exports" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#exports",
            "github.linksheet.wiki.privacy.telemetry" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#telemetry",
            "github.linksheet.wiki.privacy.amp2html" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#amp2html",
            "github.linksheet.wiki.privacy.follow-redirects" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#follow-redirects",
            "github.linksheet.wiki.privacy.downloader" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#downloader",
            "github.linksheet.wiki.device-issues.xiaomi" to "https://github.com/LinkSheet/LinkSheet/wiki/Device‐specific-issues#xiaomimiui",
            "github.repository.openlinkwith" to "https://github.com/tasomaniac/OpenLinkWith",
            "github.repository.mastodonredirect" to "https://github.com/zacharee/MastodonRedirect",
            "github.repository.seal" to "https://github.com/JunkFood02/Seal",
            "github.repository.gmsflags" to "https://github.com/polodarb/GMS-Flags",
            "github.repository.libredirect" to "https://github.com/libredirect/libredirect",
            "github.repository.fastforward" to "https://github.com/FastForwardTeam/FastForward",
            "github.org.clearurls" to "https://github.com/ClearURLs",
            "web.shizuku.download" to "https://shizuku.rikka.app/download",
            "web.supabase.privacy" to "https://supabase.com/privacy",
            "web.aptabase" to "https://aptabase.com"
        )
    )

    init {
        finalize()
    }
}
