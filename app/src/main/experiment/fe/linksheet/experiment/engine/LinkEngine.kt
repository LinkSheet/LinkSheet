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


fun createPipeline(
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    client: HttpClient,
    libRedirectResolver: LibRedirectResolver,
    cacheRepository: CacheRepository,
): Pipeline {
    val hook = object : BeforeStepHook {
        override fun <Result : StepResult> onBeforeRun(step: PipelineStep<Result>, mutUrl: String) {

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
                source = FollowRedirectsLocalSource(client = client),
                cacheRepository = cacheRepository,
                allowDarknets = { false },
                followOnlyKnownTrackers = { true },
                useLocalCache = { true }
            ),
            Amp2HtmlLinkResolver(
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
    private val beforeStepHooks = hooks.filterIsInstance<BeforeStepHook>()
    private val afterStepHooks = hooks.filterIsInstance<AfterStepHook>()

    private suspend fun <Result : StepResult> handleStep(step: PipelineStep<Result>, mutUrl: String): String {
        beforeStepHooks.forEach { it.onBeforeRun(step, mutUrl) }

        val result = step.run(mutUrl)
        val hasNewUrl = result != null && result.url != mutUrl

        afterStepHooks.forEach { it.onAfterRun(step, mutUrl, result) }
        if (!hasNewUrl) return mutUrl

        return when (step) {
            is InPlaceStep -> result.url
            else -> run(result.url)
        }
    }

    suspend fun run(url: String): String {
        var mutUrl = url
        for (step in steps) {
            mutUrl = handleStep(step, mutUrl)
        }

        return mutUrl
    }
}
