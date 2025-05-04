package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.DatabaseTest
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.assert.assertSuccess
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PreviewLocalSourceTest : DatabaseTest() {
    companion object {
        private const val URL = "https://linksheet.app"
    }

    private val cacheRepository by lazy {
        CacheRepository(
            database.htmlCacheDao(),
            database.previewCacheDao(),
            database.resolvedUrlCacheDao(),
            database.resolveTypeDao(),
            database.urlEntryDao(),
            now = { unixMillisOf(2025) }
        )
    }

    @Test
    fun `bad request`() = runTest {
        val mockEngine = MockEngine {
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
