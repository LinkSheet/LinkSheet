package fe.linksheet.experiment.engine.modifier

import fe.fastforwardkt.FastForward
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow

class FastForwardLinkModifier : LinkModifier<FastForwardModifyOutput> {
    override val id = EngineStepId.FastForward
    override suspend fun warmup() {
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl): FastForwardModifyOutput? {
        val result = FastForward.getRuleRedirect(url.toString())
        return result?.let { FastForwardModifyOutput(it.toStdUrlOrThrow()) }
    }
}

data class FastForwardModifyOutput(override val url: StdUrl) : StepResult
