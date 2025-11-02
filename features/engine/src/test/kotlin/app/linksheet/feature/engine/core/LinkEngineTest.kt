@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.engine.core

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.database.repository.CacheRepository
import app.linksheet.feature.engine.core.modifier.ClearURLsLinkModifier
import app.linksheet.feature.engine.core.modifier.EmbedLinkModifier
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlLinkResolver
import app.linksheet.feature.engine.core.resolver.amp2html.Amp2HtmlLocalSource
import app.linksheet.feature.engine.core.resolver.followredirects.FollowRedirectsLinkResolver
import app.linksheet.feature.engine.core.resolver.followredirects.FollowRedirectsLocalSource
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class LinkEngineTest : BaseUnitTest {
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

    @Test
    fun test() = runTest(dispatcher) {
//        initFileSystem(URI("jar:file:/dummy"))
        // TODO: Test should not require real http
        val client = HttpClient(OkHttp)

        val engine = LinkEngine(
            steps = listOf(
                EmbedLinkModifier(
                    enabled = { true },
                    ioDispatcher = dispatcher
                ),
                ClearURLsLinkModifier(
                    enabled = { true },
                    ioDispatcher = dispatcher
                ),
                FollowRedirectsLinkResolver(
                    enabled = { true },
                    ioDispatcher = dispatcher,
                    source = FollowRedirectsLocalSource(client = client),
                    cacheRepository = cacheRepository,
                    allowDarknets = { false },
                    allowNonPublic = { false },
                    followOnlyKnownTrackers = { true },
                    useLocalCache = { true }
                ),
                Amp2HtmlLinkResolver(
                    enabled = { true },
                    ioDispatcher = dispatcher,
                    source = Amp2HtmlLocalSource(client = client),
                    cacheRepository = cacheRepository,
                    allowDarknets = { false },
                    allowNonPublic = { false },
                    useLocalCache = { true }
                )
            ),
            dispatcher = dispatcher
        )

        val result = engine.process("https://t.co/Id9w9cFcQw".toStdUrlOrThrow())
        assertThat(result.second)
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
