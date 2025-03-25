package fe.linksheet.experiment.engine

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.DatabaseTest
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.std.time.unixMillisOf
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class PipelineTest : DatabaseTest() {
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

    private val defaultRepository by lazy {
        LibRedirectDefaultRepository(
            dao = database.libRedirectDefaultDao()
        )
    }

    private val stateRepository by lazy {
        LibRedirectStateRepository(
            dao = database.libRedirectServiceStateDao()
        )
    }

    @Test
    fun test() = runTest(dispatcher) {
        val httpClient = HttpClient(OkHttp)

        val pipeline = createPipeline(
            ioDispatcher = dispatcher,
            client = httpClient,
            libRedirectResolver = LibRedirectResolver(
                defaultRepository = defaultRepository,
                stateRepository = stateRepository
            ),
            cacheRepository = cacheRepository
        )

        val url = pipeline.run("https://t.co/Id9w9cFcQw")
        assertThat(url).isEqualTo("https://www.technologyreview.com/2021/03/26/1021318/google-security-shut-down-counter-terrorist-us-ally/")
    }

    @Test
    fun test2() = runTest(dispatcher) {
        val client = HttpClient(OkHttp)
        val hook = object : BeforeStepHook {
            override fun <Result : StepResult> onBeforeRun(step: EngineStep<Result>, url: String) {
                println("$step $url")
            }
        }
        val pipeline = LinkEngine(
            listOf(
                EmbedLinkModifier(
                    ioDispatcher = dispatcher
                ),
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
//            listOf(hook)
        )

        val url = pipeline.run("https://t.co/Id9w9cFcQw")
        assertThat(url).isEqualTo("https://www.technologyreview.com/2021/03/26/1021318/google-security-shut-down-counter-terrorist-us-ally/")
    }
}
