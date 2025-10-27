package app.linksheet.feature.engine.engine.modifier

import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.context.IgnoreLibRedirectExtra
import app.linksheet.feature.engine.engine.context.hasExtra
import app.linksheet.feature.engine.engine.fetcher.ContextResult
import app.linksheet.feature.engine.engine.fetcher.ContextResultId
import app.linksheet.feature.engine.engine.step.EngineStepId
import app.linksheet.feature.engine.engine.step.StepResult
import app.linksheet.feature.libredirect.LibRedirectResolver
import app.linksheet.feature.libredirect.LibRedirectResult
import fe.linksheet.extension.toStdUrlOrThrow
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
