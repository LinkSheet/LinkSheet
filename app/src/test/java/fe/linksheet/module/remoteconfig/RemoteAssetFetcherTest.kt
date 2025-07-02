package fe.linksheet.module.remoteconfig

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.assert.assertFailure
import fe.std.result.assert.assertSuccess
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.test.runTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class RemoteAssetFetcherTest : BaseUnitTest {
    private fun MockRequestHandleScope.respondJson(json: String): HttpResponseData {
        return respond(json, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }

    private fun createClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { gson() }
        }
    }


    @org.junit.Test
    fun test() = runTest {
        val assetsJson = """{
        |  "github.linksheet.wiki.privacy.logs": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#logs",
        |  "github.org.clearurls": "https://github.com/ClearURLs",
        |  "web.aptabase": "https://aptabase.com"
        |}""".trimMargin()

        val client = createClient { respondJson(assetsJson) }
        val fetcher = RemoteConfigClient("https://linksheet.app", "Dummy User-Agent", client)
        val assets = fetcher.fetchLinkAssets()
        assertSuccess(assets).isEqualTo(
            mapOf(
                "github.linksheet.wiki.privacy.logs" to "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#logs",
                "github.org.clearurls" to "https://github.com/ClearURLs",
                "web.aptabase" to "https://aptabase.com"
            )
        )
    }

    @org.junit.Test
    fun `test bad path`() = runTest {
        val assetsJson = """
        |"{
        |  "github.linksheet.wiki.privacy.logs": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#logs",
        |  "github.linksheet.wiki.privacy.exports": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#exports",
        |  "github.linksheet.wiki.privacy.telemetry": "https://github.com/LinkSheet/LinkSheet/wiki/Privacy#telemetry",
        |}"
        |""".trimMargin()

        val client = createClient { respondJson(assetsJson) }
        val fetcher = RemoteConfigClient("https://linksheet.app", "Dummy User-Agent", client)
        val assets = fetcher.fetchLinkAssets()
        assertFailure(assets)
    }

    @org.junit.Test
    fun `test bad path 2`() = runTest {
        val client = createClient { respondError(HttpStatusCode.BadRequest) }
        val fetcher = RemoteConfigClient("https://linksheet.app", "Client", client)
        val assets = fetcher.fetchLinkAssets()
        assertFailure(assets)
    }
}
