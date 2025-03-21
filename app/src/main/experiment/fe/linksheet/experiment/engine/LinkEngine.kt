package fe.linksheet.experiment.engine

import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.modifier.LibRedirectLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.resolver.LibRedirectResolver
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive


fun createPipeline(
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    client: HttpClient,
    libRedirectResolver: LibRedirectResolver,
    cacheRepository: CacheRepository,
): Pipeline {
    val hook = object : BeforeStepHook {
        override fun <R : StepResult> onBeforeRun(step: PipelineStep<R>, url: String) {

        }
    }

    val pipeline = Pipeline(
        listOf(
            EmbedLinkModifier(
                ioDispatcher = ioDispatcher
            ),
            LibRedirectLinkModifier(
                ioDispatcher = ioDispatcher,
                resolver = libRedirectResolver
            ),
            ClearURLsLinkModifier(ioDispatcher = ioDispatcher),
            FollowRedirectsLinkResolver(
                ioDispatcher = ioDispatcher,
                source = FollowRedirectsLocalSource(client = client),
                cacheRepository = cacheRepository,
                allowDarknets = { false },
                followOnlyKnownTrackers = { true },
                useLocalCache = { true }
            ),
            Amp2HtmlLinkResolver(
                ioDispatcher = ioDispatcher,
                source = Amp2HtmlLocalSource(client = client),
                cacheRepository = cacheRepository,
                useLocalCache = { true }
            )
        )
    )

    return pipeline
}

class Pipeline(
    val steps: List<PipelineStep<*>>,
    val hooks: List<PipelineHook> = emptyList()
) {
    // TODO: These should probably
    // a) be invoked asynchronously
    // b) have some sort of veto-capability?
    private val beforeStepHooks = hooks.filterIsInstance<BeforeStepHook>()
    private val afterStepHooks = hooks.filterIsInstance<AfterStepHook>()

    private suspend fun <R : StepResult> runStep(
        step: PipelineStep<R>,
        url: String
    ): Pair<Boolean, String> {
        beforeStepHooks.forEach { it.onBeforeRun(step, url) }

        val result = step.run(url)
        val hasNewUrl = result != null && result.url != url

        afterStepHooks.forEach { it.onAfterRun(step, url, result) }
        if (!hasNewUrl) return false to url

        return true to result.url
    }

    suspend fun run(url: String): String = coroutineScope scope@{
        var mutUrl = url
        for (step in steps) {
            if (!isActive) break
            val (hasNewUrl, resultUrl) = runStep(step, mutUrl)
            if (!hasNewUrl) continue

            if (step !is InPlaceStep) return@scope run(resultUrl)
            mutUrl = resultUrl
        }

        mutUrl
    }
}
