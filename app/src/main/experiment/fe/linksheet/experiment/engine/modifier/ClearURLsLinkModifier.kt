package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearUrls
import fe.clearurlskt.loader.BundledClearURLConfigLoader
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.ModifyInput
import fe.linksheet.experiment.engine.ModifyOutput

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
