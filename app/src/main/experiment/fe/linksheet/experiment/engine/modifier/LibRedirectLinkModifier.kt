package fe.linksheet.experiment.engine.modifier

import android.net.Uri
import fe.linksheet.experiment.engine.EngineStepId
import fe.linksheet.experiment.engine.EngineRunContext
import fe.linksheet.experiment.engine.StepResult
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.LibRedirectResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val resolver: LibRedirectResolver,
    private val useJsEngine: () -> Boolean = { false }
) : LinkModifier<LibRedirectModifyOutput> {
    override val id = EngineStepId.LibRedirect

    override suspend fun warmup() = withContext(ioDispatcher) {
        resolver.warmup()
    }

    override suspend fun EngineRunContext.runStep(url: String): LibRedirectModifyOutput = withContext(ioDispatcher) {
//        val ignoreLibRedirectExtra = intent.getBooleanExtra(LibRedirectDefault.libRedirectIgnore, false)
//        if (ignoreLibRedirectExtra) {
//            intent.extras?.remove(LibRedirectDefault.libRedirectIgnore)
//        }

//        if (ignoreLibRedirectExtra && ignoreLibRedirectButton) return@withContext null
        val jsEngine = useJsEngine()
        val result = resolver.resolve(url, jsEngine)
        result.toModifyOutput(url)
    }
}

sealed interface LibRedirectModifyOutput : StepResult {
    data class Redirected(val originalUri: Uri, val redirectedUri: Uri) : LibRedirectModifyOutput {
        override val url = redirectedUri.toString()
    }

    data class NotRedirected(override val url: String) : LibRedirectModifyOutput
}

fun LibRedirectResult.toModifyOutput(url: String): LibRedirectModifyOutput {
    return when (this) {
        is LibRedirectResult.Redirected -> LibRedirectModifyOutput.Redirected(originalUri, redirectedUri)
        is LibRedirectResult.NotRedirected -> LibRedirectModifyOutput.NotRedirected(url)
    }
}
