package fe.linksheet.module.remoteconfig

import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class RemoteAssetFetcherTest : BaseUnitTest {
    @Test
    fun test() = runTest {
        val assetsJson = """{
          "github.linksheet.wiki.privacy.logs": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#logs",
          "github.linksheet.wiki.privacy.exports": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#exports",
          "github.linksheet.wiki.privacy.telemetry": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#telemetry",
          "github.linksheet.wiki.privacy.amp2html": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#amp2html",
          "github.linksheet.wiki.privacy.follow-redirects": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#follow-redirects",
          "github.linksheet.wiki.privacy.downloader": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#downloader",
          "github.linksheet.wiki.device-issues.xiaomi": "https://github.com/LinkSheet/LinkSheet/wiki/Device‐specific-issues#xiaomimiui",
          "github.repository.openlinkwith": "https://github.com/tasomaniac/OpenLinkWith",
          "github.repository.mastodonredirect": "https://github.com/zacharee/MastodonRedirect",
          "github.repository.seal": "https://github.com/JunkFood02/Seal",
          "github.repository.gmsflags": "https://github.com/polodarb/GMS-Flags",
          "github.repository.libredirect": "https://github.com/libredirect/libredirect",
          "github.repository.fastforward": "https://github.com/FastForwardTeam/FastForward",
          "github.org.clearurls": "https://github.com/ClearURLs",
          "web.shizuku.download": "https://shizuku.rikka.app/download",
          "web.supabase.privacy": "https://supabase.com/privacy",
          "web.aptabase": "https://aptabase.com"
        }"""
        val engine = MockEngine {
            respond(assetsJson, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }

        val client = HttpClient(engine) {
            install(ContentNegotiation) { gson() }
        }

        val fetcher = RemoteConfigClient("http://test.app", "Dummy User-Agent", client)
        val assets = fetcher.fetchLinkAssets()
        println(assets)
    }
}
