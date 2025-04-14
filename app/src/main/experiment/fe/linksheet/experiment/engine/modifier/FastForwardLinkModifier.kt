package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.EngineStepId
import fe.linksheet.experiment.engine.EngineRunContext
import fe.linksheet.experiment.engine.StepResult

class FastForwardLinkModifier : LinkModifier<FastForwardModifyOutput> {
    override val id = EngineStepId.FastForward
    override suspend fun warmup() {
    }

    override suspend fun EngineRunContext.runStep(url: String): FastForwardModifyOutput? {
        val result = FastForward.getRuleRedirect(url)
        return result?.let { FastForwardModifyOutput(it) }
    }
}

data class FastForwardModifyOutput(override val url: String) : StepResult
