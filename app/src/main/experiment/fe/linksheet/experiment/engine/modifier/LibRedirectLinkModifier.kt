package fe.linksheet.experiment.engine.modifier

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.IgnoreLibRedirectExtra
import fe.linksheet.experiment.engine.context.hasExtra
import fe.linksheet.experiment.engine.fetcher.ContextResult
import fe.linksheet.experiment.engine.fetcher.ContextResultId
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.linksheet.extension.std.toStdUrlOrThrow
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.LibRedirectResult
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val useJsEngine: () -> Boolean = { false },
    override val enabled: () -> Boolean
) : LinkModifier<LibRedirectModifyOutput> {
    override val id = EngineStepId.LibRedirect

    override suspend fun warmup() = withContext(ioDispatcher) {
        resolver.warmup()
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl): LibRedirectModifyOutput = withContext(ioDispatcher) {
        if (hasExtra<IgnoreLibRedirectExtra>()) {
            return@withContext LibRedirectModifyOutput.Ignored(url)
        }

        val jsEngine = useJsEngine()
        val result = resolver.resolve(url.toString(), jsEngine)
        val output = result.toModifyOutput(url)
        val contextResult = result.wrapInContextResult()
        put(ContextResultId.LibRedirect, contextResult)

        output
    }
}

class LibRedirectContextResult(val wrapped: LibRedirectResult) : ContextResult

private fun LibRedirectResult.wrapInContextResult(): LibRedirectContextResult {
    return LibRedirectContextResult(this)
}

sealed interface LibRedirectModifyOutput : StepResult {
    data class Ignored(override val url: StdUrl) : LibRedirectModifyOutput
    data class Redirected(val originalUrl: StdUrl, val redirectedUrl: StdUrl) : LibRedirectModifyOutput {
        override val url = redirectedUrl
    }

    data class NotRedirected(override val url: StdUrl) : LibRedirectModifyOutput
}

fun LibRedirectResult.toModifyOutput(url: StdUrl): LibRedirectModifyOutput {
    return when (this) {
        is LibRedirectResult.Redirected -> LibRedirectModifyOutput.Redirected(
            originalUri.toStdUrlOrThrow(),
            redirectedUri.toStdUrlOrThrow()
        )

        is LibRedirectResult.NotRedirected -> LibRedirectModifyOutput.NotRedirected(url)
    }
}
