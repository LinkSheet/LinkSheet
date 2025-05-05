package fe.linksheet.experiment.engine

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.SourceAppExtra
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.experiment.engine.rule.BaseRuleEngineTest
import fe.linksheet.module.repository.CacheRepository
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class LinkEngineTest : BaseRuleEngineTest() {
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

    @Test
    fun test() = runTest(dispatcher) {
        // TODO: Test should not require real http
        val client = HttpClient(OkHttp)

        val engine = LinkEngine(
            steps = listOf(
                EmbedLinkModifier(ioDispatcher = dispatcher),
                ClearURLsLinkModifier(ioDispatcher = dispatcher),
                FollowRedirectsLinkResolver(
                    ioDispatcher = dispatcher,
                    source = FollowRedirectsLocalSource(client = client),
                    cacheRepository = cacheRepository,
                    allowDarknets = { false },
                    followOnlyKnownTrackers = { true },
                    useLocalCache = { true }
                ),
                Amp2HtmlLinkResolver(
                    ioDispatcher = dispatcher,
                    source = Amp2HtmlLocalSource(client = client),
                    cacheRepository = cacheRepository,
                    useLocalCache = { true }
                )
            ),
            dispatcher = dispatcher
        )

        val result = engine.process("https://t.co/Id9w9cFcQw".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://www.technologyreview.com/2021/03/26/1021318/google-security-shut-down-counter-terrorist-us-ally/")
    }

    @Test
    fun test2() = runTest(dispatcher) {
        val engine = LinkEngine(
            steps = listOf(
                ClearURLsLinkModifier(ioDispatcher = dispatcher)
            ),
        )

        val url = "https://www.instagram.com/reel/42e3d61b7/?igsh=659e8fac5ca9ce8d009563a584fd5602d15ff100a1f21be4aa7ea2295625c4cc".toStdUrlOrThrow()
        val extra = SourceAppExtra("com.google.chrome")
        val context = DefaultEngineRunContext(extra)
        val result = engine.process(url, context)
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://www.instagram.com/reel/42e3d61b7/")
    }
}
