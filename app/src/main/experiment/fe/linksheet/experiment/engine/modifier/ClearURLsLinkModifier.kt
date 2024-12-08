package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.linksheet.experiment.engine.ModifyInput
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.ModifyOutput

class ClearURLsLinkModifier : LinkModifier {
    companion object {
        private val clearUrlProviders by lazy { ClearURLLoader.loadBuiltInClearURLProviders() }
    }

    override suspend fun modify(data: ModifyInput): ModifyOutput {
        val result = ClearURL.clearUrl(data.url, clearUrlProviders)
        return ModifyOutput(result)
    }
}
