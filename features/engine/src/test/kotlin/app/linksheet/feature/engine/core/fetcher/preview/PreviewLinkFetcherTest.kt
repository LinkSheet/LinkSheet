@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine.core.fetcher.preview

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.core.EngineDatabaseTestRule
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.IResult
import fe.std.result.success
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

//TODO: Needs to be moved to [feature-engine] module, but has dependency on [DatabaseTestRule], which requires the application database
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class PreviewLinkFetcherTest : BaseUnitTest {
    companion object {
        private const val PREVIEW_URL = "https://linksheet.app"
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
            clock = Clock.System
        )
    }

    private val source = object : PreviewSource {
        override suspend fun fetch(urlString: String): IResult<PreviewFetchResult> {
            return PreviewFetchResult.NonHtmlPage(PREVIEW_URL).success
        }

        override suspend fun parseHtml(htmlText: String, urlString: String): IResult<PreviewFetchResult> {
            TODO("Not yet implemented")
        }
    }

    @Test
    fun test() = runTest {
        val fetcher = PreviewLinkFetcher(
            source = source,
            cacheRepository = cacheRepository,
            useLocalCache = { true }
        )

        val result = fetcher.fetch(PREVIEW_URL.toStdUrlOrThrow())
        assertThat(result).isNotNull().isInstanceOf<PreviewFetchResult.NonHtmlPage>()
    }
}
