package fe.linksheet.feature.engine.engine.fetcher.preview

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.engine.fetcher.preview.PreviewFetchResult
import app.linksheet.feature.engine.engine.fetcher.preview.PreviewLinkFetcher
import app.linksheet.feature.engine.engine.fetcher.preview.PreviewLocalSource
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.DatabaseTestRule
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.assert.assertSuccess
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class PreviewLocalSourceTest : BaseUnitTest {
    companion object {
        private const val URL = "https://linksheet.app"
    }

    @get:Rule
    private val rule = DatabaseTestRule(applicationContext)

    private val cacheRepository by lazy {
        CacheRepository(
            rule.database.htmlCacheDao(),
            rule.database.previewCacheDao(),
            rule.database.resolvedUrlCacheDao(),
            rule.database.resolveTypeDao(),
            rule.database.urlEntryDao(),
            now = { unixMillisOf(2025) }
        )
    }

    @Test
    fun `bad request`() = runTest {
        val mockEngine = MockEngine.Companion {
            respondBadRequest()
        }

        val source = PreviewLocalSource(HttpClient(mockEngine))
        val result = source.fetch(URL)

        assertSuccess(result)
            .isInstanceOf<PreviewFetchResult.NonHtmlPage>()
            .prop(PreviewFetchResult.NonHtmlPage::url)
            .isEqualTo(URL)
    }

    @Test
    fun test() = runTest {
        val client = HttpClient(OkHttp)
        val fetcher = PreviewLinkFetcher(
            ioDispatcher = Dispatchers.IO,
            source = PreviewLocalSource(
                client = client
            ),
            cacheRepository = cacheRepository,
            useLocalCache = { true }
        )

        val result = fetcher.fetch("https://www.youtube.com/watch?v=sozAABvDToY".toStdUrlOrThrow())
        println(result)
    }
}
