package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader

class ClearURLsLinkModifier : LinkModifier {
    companion object {
        private val clearUrlProviders by lazy { BundledClearURLConfigLoader.load().getOrNull() }
    }

    private val clearUrls = clearUrlProviders?.let { ClearUrls(it) }

    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = clearUrls?.clearUrl(data.url)?.first
        return result?.let { ModifyOutput(it) }
    }
}
