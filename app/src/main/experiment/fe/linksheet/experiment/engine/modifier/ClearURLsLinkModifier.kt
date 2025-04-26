package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearUrlOperation
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.linksheet.experiment.engine.EngineStepId
import fe.linksheet.experiment.engine.InPlaceStep
import fe.linksheet.experiment.engine.EngineRunContext
import fe.linksheet.experiment.engine.StepResult
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearURLsLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LinkModifier<ClearURLsModifyOutput>, InPlaceStep {
    override val id = EngineStepId.ClearURLs

    companion object {
        private val clearUrlProviders by lazy { BundledClearURLConfigLoader.load().getOrNull() }
    }

    private val clearUrls by lazy {
        clearUrlProviders?.let { ClearUrls(it) }
    }

    override suspend fun warmup() = withContext(ioDispatcher) {
        clearUrls
        Unit
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl) = withContext(ioDispatcher) {
        val result = clearUrls?.clearUrl(url.toString())
        result?.let { (url, operations) -> ClearURLsModifyOutput(url.toStdUrlOrThrow(), operations) }
    }
}

data class ClearURLsModifyOutput(
    override val url: StdUrl,
    val operations: List<ClearUrlOperation>
) : StepResult
