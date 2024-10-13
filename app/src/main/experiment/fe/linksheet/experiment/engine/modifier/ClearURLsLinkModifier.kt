package fe.linksheet.experiment.engine.modifier

import fe.clearurlskt.ClearURL
import fe.clearurlskt.ClearURLLoader
import fe.linksheet.experiment.engine.Input
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.Output

class ClearURLsLinkModifier : LinkModifier {
    companion object {
        private val clearUrlProviders by lazy { ClearURLLoader.loadBuiltInClearURLProviders() }
    }

    override fun modify(data: Input): Output {
        val result = ClearURL.clearUrl(data.url, clearUrlProviders)
        return Output(result)
    }
}
