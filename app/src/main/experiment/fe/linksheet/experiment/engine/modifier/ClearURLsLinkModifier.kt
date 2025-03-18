package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearUrlOperation
import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.linksheet.experiment.engine.InPlaceStep
import fe.linksheet.experiment.engine.StepResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearURLsLinkModifier(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LinkModifier<ClearURLsModifyOutput>, InPlaceStep {

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

    override suspend fun run(url: String): ClearURLsModifyOutput? {
        val result = clearUrls?.clearUrl(url)
        return result?.let { (urls, operations) -> ClearURLsModifyOutput(urls, operations) }
    }
}

data class ClearURLsModifyOutput(
    override val url: String,
    val operations: List<ClearUrlOperation>
) : StepResult
