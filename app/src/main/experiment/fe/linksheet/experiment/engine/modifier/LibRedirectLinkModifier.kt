package fe.linksheet.experiment.engine.modifier

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.IgnoreLibRedirect
import fe.linksheet.experiment.engine.context.hasExtra
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.linksheet.extension.std.toStdUrlOrThrow
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.LibRedirectResult
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LibRedirectLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val useJsEngine: () -> Boolean = { false }
) : LinkModifier<LibRedirectModifyOutput> {
    override val id = EngineStepId.LibRedirect

    override suspend fun warmup() = withContext(ioDispatcher) {
        resolver.warmup()
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl): LibRedirectModifyOutput = withContext(ioDispatcher) {
        if (hasExtra<IgnoreLibRedirect>()) {
            return@withContext LibRedirectModifyOutput.Ignored(url)
        }

        val jsEngine = useJsEngine()
        val result = resolver.resolve(url.toString(), jsEngine)
        result.toModifyOutput(url)
    }
}

sealed interface LibRedirectModifyOutput : StepResult {
    data class Ignored(override val url: StdUrl) : LibRedirectModifyOutput
    data class Redirected(val originalUri: StdUrl, val redirectedUri: StdUrl) : LibRedirectModifyOutput {
        override val url = redirectedUri
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
