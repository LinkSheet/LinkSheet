package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.ModifyInput
import fe.linksheet.experiment.engine.LinkModifier
import fe.linksheet.experiment.engine.ModifyOutput

class FastForwardLinkModifier : LinkModifier {
    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = FastForward.getRuleRedirect(data.url)
        return result?.let { ModifyOutput(it) }
    }
}
