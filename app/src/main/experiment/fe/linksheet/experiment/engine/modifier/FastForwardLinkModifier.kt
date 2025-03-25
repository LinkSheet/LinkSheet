package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.StepResult

class FastForwardLinkModifier : LinkModifier<FastForwardModifyOutput> {
    override suspend fun warmup() {
    }

    override suspend fun run(url: String): FastForwardModifyOutput? {
        val result = FastForward.getRuleRedirect(url)
        return result?.let { FastForwardModifyOutput(it) }
    }
}

data class FastForwardModifyOutput(override val url: String) : StepResult
