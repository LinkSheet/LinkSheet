package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.Input
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.Output

class FastForwardLinkModifier : LinkModifier {
    override fun modify(data: Input): Output? {
        val result = FastForward.getRuleRedirect(data.url)
        return result?.let { Output(it) }
    }
}
