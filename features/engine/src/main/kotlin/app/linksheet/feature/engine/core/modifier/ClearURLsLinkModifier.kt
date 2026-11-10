package app.linksheet.feature.engine.core.modifier


import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.step.EngineStepId
import app.linksheet.feature.engine.core.step.InPlaceStep
import app.linksheet.feature.engine.core.step.StepResult
import fe.clearurlskt.ClearUrlOperation
import fe.clearurlskt.ClearUrlSettings
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.config.provider.Provider
import app.linksheet.util.SuspendingLazy
import fe.std.result.StdResult
import fe.std.result.isFailure
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearURLsLinkModifier(
    private val getProviders: suspend () -> StdResult<List<Provider>>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    override val enabled: () -> Boolean,
    val skipUnchanged: () -> Boolean,
) : LinkModifier<ClearURLsModifyOutput>, InPlaceStep {
    override val id = EngineStepId.ClearURLs

    private val clearUrls = SuspendingLazy {
        val result = getProviders()
        // TODO: Log this
        if (result.isFailure()) return@SuspendingLazy null
        ClearUrls(result.value)
    }

    override suspend fun warmup() = withContext(ioDispatcher) {
        clearUrls.value()
        Unit
    }

    context(context: EngineRunContext)
    override suspend fun runStep(url: StdUrl) = withContext(ioDispatcher) {
        val result = clearUrls.value()?.clearUrl(
            url = url.toString(),
            settings = ClearUrlSettings(skipUnchanged = skipUnchanged())
        )
        result?.let { (url, operations) ->
            ClearURLsModifyOutput(
                url.toStdUrlOrThrow(),
                operations
            )
        }
    }
}

data class ClearURLsModifyOutput(
    override val url: StdUrl,
    val operations: List<ClearUrlOperation>
) : StepResult
