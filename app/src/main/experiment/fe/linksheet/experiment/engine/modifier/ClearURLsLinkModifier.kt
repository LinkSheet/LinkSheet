package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearURLsLinkModifier(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : LinkModifier {
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

    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = clearUrls?.clearUrl(data.url)?.first
        return result?.let { ModifyOutput(it) }
    }
}
