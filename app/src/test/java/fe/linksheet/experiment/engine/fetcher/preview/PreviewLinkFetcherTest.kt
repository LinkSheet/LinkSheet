package fe.linksheet.experiment.engine.fetcher.preview

import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.DatabaseTest
import fe.linksheet.experiment.engine.fetcher.FetchInput
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.IResult
import fe.std.result.success
import fe.std.time.unixMillisOf
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PreviewLinkFetcherTest : DatabaseTest() {
    companion object{
        private const val PREVIEW_URL = "https://linksheet.app"
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

    private val source = object : PreviewSource {
        override suspend fun fetch(urlString: String): IResult<PreviewResult> {
            return PreviewResult.NonHtmlPage(PREVIEW_URL).success
        }

        override suspend fun parseHtml(htmlText: String, urlString: String) {
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

       val result = fetcher.fetch(FetchInput(PREVIEW_URL))
    }
}
