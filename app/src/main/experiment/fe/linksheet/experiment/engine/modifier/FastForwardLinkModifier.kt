package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward

class FastForwardLinkModifier : LinkModifier {
    override suspend fun modify(data: ModifyInput): ModifyOutput? {
        val result = FastForward.getRuleRedirect(data.url)
        return result?.let { ModifyOutput(it) }
    }
}
