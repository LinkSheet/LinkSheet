package fe.linksheet.experiment.engine

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.DatabaseTestRule
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.experiment.engine.rule.assertResult
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class LinkEngineTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

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

    @org.junit.Test
    fun test() = runTest(dispatcher) {
//        initFileSystem(URI("jar:file:/dummy"))
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

//    @org.junit.Test
//    fun test2() = runTest(dispatcher) {
//        val engine = LinkEngine(
//            steps = listOf(
//                ClearURLsLinkModifier(ioDispatcher = dispatcher)
//            ),
//        )
//
//        val url = "https://www.instagram.com/reel/42e3d61b7/?igsh=659e8fac5ca9ce8d009563a584fd5602d15ff100a1f21be4aa7ea2295625c4cc".toStdUrlOrThrow()
//        val extra = SourceAppExtra("com.google.chrome")
//        val context = DefaultEngineRunContext(extra)
//        val result = engine.process(url, context)
//        assertResult(result)
//            .isInstanceOf<UrlEngineResult>()
//            .prop(UrlEngineResult::url)
//            .transform { it.toString() }
//            .isEqualTo("https://www.instagram.com/reel/42e3d61b7/")
//    }
}
