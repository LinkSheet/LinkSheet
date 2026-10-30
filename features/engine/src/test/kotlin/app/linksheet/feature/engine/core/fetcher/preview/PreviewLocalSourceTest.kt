@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine.core.fetcher.preview

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.core.EngineDatabaseTestRule
import app.linksheet.feature.engine.database.repository.CacheRepository
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.assert.assertSuccess
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.BAKLAVA])
internal class PreviewLocalSourceTest : BaseUnitTest {
    companion object {
        private const val URL = "https://linksheet.app"
    }


    @get:Rule
    private val rule = EngineDatabaseTestRule(applicationContext)

    private val cacheRepository by lazy {
        CacheRepository(
            rule.database.htmlCacheDao(),
            rule.database.previewCacheDao(),
            rule.database.resolvedUrlCacheDao(),
            rule.database.resolveTypeDao(),
            rule.database.urlEntryDao(),
            clock = Clock.System,
            incognitoMode = { false }
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
