@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine.core.resolver.amp2html

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlLinkResolver
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlResult
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlSource
import app.linksheet.feature.engine.database.entity.ResolveType
import app.linksheet.feature.engine.database.entity.ResolvedUrl
import app.linksheet.feature.engine.database.entity.UrlEntry
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.core.EngineDatabaseTestRule
import app.linksheet.feature.engine.core.resolver.ResolveOutput
import app.linksheet.feature.engine.core.rule.withTestRunContext
import app.linksheet.feature.engine.database.entity.CachedHtml
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.prop
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.result.IResult
import fe.std.result.success
import fe.std.result.unaryPlus
import fe.std.time.unixMillisOf
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class Amp2HtmlLinkResolverTest : BaseUnitTest {
    companion object {
        private val URL = "https://amp.cnn.com/cnn/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html".toStdUrlOrThrow()
        private val RESOLVED_URL = "https://www.cnn.com/2023/06/19/europe/titanic-shipwreck-vessel-missing-intl/index.html".toStdUrlOrThrow()
    }

    private val dispatcher = StandardTestDispatcher()

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

    private val source = createSource(RESOLVED_URL, "<html></html>")

    private fun createSource(resolvedUrl: StdUrl, fakeHtmlText: String): Amp2HtmlSource {
        return object : Amp2HtmlSource {
            override suspend fun resolve(url: StdUrl): IResult<Amp2HtmlResult> {
                return Amp2HtmlResult.NonAmpLink(RESOLVED_URL, fakeHtmlText).success
            }

            override suspend fun parseHtml(
                htmlText: String,
                url: StdUrl
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

        val result = withTestRunContext(resolver) { it.runStep(URL) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo(RESOLVED_URL)

        val entry = rule.database.urlEntryDao().getUrlEntry(URL.toString())
        assertThat(entry).isNotNull()

        val resolved = rule.database.resolvedUrlCacheDao().getResolved(entry!!.id, ResolveType.Companion.Amp2Html.id)
        assertThat(resolved).isNull()
    }

    @Test
    fun `return cached url if present`() = runTest(dispatcher) {
        val resolver = Amp2HtmlLinkResolver(
            ioDispatcher = dispatcher,
            source = createSource("https://not-from-cache.com".toStdUrlOrThrow(), "<html></html>"),
            cacheRepository = cacheRepository,
            useLocalCache = { true },
        )

        val testResolvedUrl = "https://linksheet.app"
        val entry = UrlEntry(1, url = URL.toString())
        val resolved = ResolvedUrl(entry.id, ResolveType.Companion.Amp2Html.id, testResolvedUrl)

        rule.database.urlEntryDao().insertReturningId(entry)
        rule.database.resolvedUrlCacheDao().insertReturningId(resolved)

        val result = withTestRunContext(resolver) { it.runStep(entry.url.toStdUrlOrThrow()) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .transform { it.toString() }
            .isEqualTo(testResolvedUrl)
    }

    @Test
    fun `use cached html if present`() = runTest(dispatcher) {
        val cachedHtml = "<html><body><h1>Cached html</h1></body></html>"
        val resolver = Amp2HtmlLinkResolver(
            ioDispatcher = dispatcher,
            source = object : Amp2HtmlSource {
                override suspend fun resolve(url: StdUrl): IResult<Amp2HtmlResult> {
                    return +Amp2HtmlResult.NonAmpLink(url, "<html><body><h1>Html not from cache</h1></body></html>")
                }

                override suspend fun parseHtml(
                    htmlText: String,
                    url: StdUrl
                ): IResult<Amp2HtmlResult> {
                    if (htmlText == cachedHtml) {
                        return +Amp2HtmlResult.NonAmpLink("https://linksheet.app".toStdUrlOrThrow(), htmlText)
                    }

                    return +Amp2HtmlResult.NonAmpLink("https://not-from-cache.com".toStdUrlOrThrow(), htmlText)
                }
            },
            cacheRepository = cacheRepository,
            useLocalCache = { true },
        )

        val entry = UrlEntry(1, url = URL.toString())
        val htmlCache = CachedHtml(entry.id, cachedHtml)

        rule.database.urlEntryDao().insertReturningId(entry)
        rule.database.htmlCacheDao().insertReturningId(htmlCache)

        val result = withTestRunContext(resolver) { it.runStep(entry.url.toStdUrlOrThrow()) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .transform { it.toString() }
            .isEqualTo("https://linksheet.app")
    }
}
