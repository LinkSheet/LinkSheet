package fe.linksheet.experiment.engine.resolver.amp2html

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.prop
import fe.linksheet.DatabaseTest
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.experiment.engine.withTestRunContext
import fe.linksheet.module.database.entity.cache.CachedHtml
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.database.entity.cache.ResolvedUrl
import fe.linksheet.module.database.entity.cache.UrlEntry
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.IResult
import fe.std.result.success
import fe.std.time.unixMillisOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class Amp2HtmlLinkResolverTest : DatabaseTest() {
    companion object {
        private const val URL = "https://amp.cnn.com/cnn/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html"
        private const val RESOLVED_URL = "https://www.cnn.com/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html"
    }

    private val dispatcher = StandardTestDispatcher()

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

    private val source = createSource(RESOLVED_URL, "<html></html>")

    private fun createSource(resolvedUrl: String, fakeHtmlText: String): Amp2HtmlSource {
        return object : Amp2HtmlSource {
            override suspend fun resolve(urlString: String): IResult<Amp2HtmlResult> {
                return Amp2HtmlResult.NonAmpLink(Companion.RESOLVED_URL, fakeHtmlText).success
            }

            override suspend fun parseHtml(
                htmlText: String,
                urlString: String
            ): IResult<Amp2HtmlResult> {
                return Amp2HtmlResult.NonAmpLink(resolvedUrl, htmlText).success
            }
        }
    }

    @Test
    fun `skip cache if disabled`() = runTest(dispatcher) {
        val resolver = Amp2HtmlLinkResolver(
            ioDispatcher = dispatcher,
            source = source,
            cacheRepository = cacheRepository,
            useLocalCache = { false },
        )

        val result = withTestRunContext { resolver.runStep(URL) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo(RESOLVED_URL)

        val entry = database.urlEntryDao().getUrlEntry(URL)
        assertThat(entry).isNotNull()

        val resolved = database.resolvedUrlCacheDao().getResolved(entry!!.id, ResolveType.Amp2Html.id)
        assertThat(resolved).isNull()
    }

    @Test
    fun `return cached url if present`() = runTest(dispatcher) {
        val resolver = Amp2HtmlLinkResolver(
            ioDispatcher = dispatcher,
            source = createSource("https://not-from-cache.com", "<html></html>"),
            cacheRepository = cacheRepository,
            useLocalCache = { true },
        )

        val testResolvedUrl = "https://linksheet.app"
        val entry = UrlEntry(1, url = URL)
        val resolved = ResolvedUrl(entry.id, ResolveType.Amp2Html.id, testResolvedUrl)

        database.urlEntryDao().insertReturningId(entry)
        database.resolvedUrlCacheDao().insertReturningId(resolved)

        val result = withTestRunContext { resolver.runStep(entry.url) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo(testResolvedUrl)
    }

    @Test
    fun `use cached html if present`() = runTest(dispatcher) {
        val cachedHtml = "<html><body><h1>Cached html</h1></body></html>"
        val resolver = Amp2HtmlLinkResolver(
            ioDispatcher = dispatcher,
            source = object : Amp2HtmlSource {
                override suspend fun resolve(urlString: String): IResult<Amp2HtmlResult> {
                    return Amp2HtmlResult.NonAmpLink(urlString, "<html><body><h1>Html not from cache</h1></body></html>").success
                }

                override suspend fun parseHtml(
                    htmlText: String,
                    urlString: String
                ): IResult<Amp2HtmlResult> {
                    if (htmlText == cachedHtml) {
                        return Amp2HtmlResult.NonAmpLink("https://linksheet.app", htmlText).success
                    }

                    return Amp2HtmlResult.NonAmpLink("https://not-from-cache.com", htmlText).success
                }
            },
            cacheRepository = cacheRepository,
            useLocalCache = { true },
        )

        val entry = UrlEntry(1, url = URL)
        val htmlCache = CachedHtml(entry.id, cachedHtml)

        database.urlEntryDao().insertReturningId(entry)
        database.htmlCacheDao().insertReturningId(htmlCache)

        val result = withTestRunContext { resolver.runStep(entry.url) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo("https://linksheet.app")
    }
}
