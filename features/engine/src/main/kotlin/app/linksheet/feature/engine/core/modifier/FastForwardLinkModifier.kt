package app.linksheet.feature.engine.core.modifier

import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.step.EngineStepId
import app.linksheet.feature.engine.core.step.StepResult
import fe.fastforwardkt.FastForward
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow

class FastForwardLinkModifier(override val enabled: () -> Boolean) : LinkModifier<FastForwardModifyOutput> {
    override val id = EngineStepId.FastForward
    override suspend fun warmup() {
    }

    override suspend fun EngineRunContext.runStep(url: StdUrl): FastForwardModifyOutput? {
        val result = FastForward.getRuleRedirect(url.toString())
        return result?.let { FastForwardModifyOutput(it.toStdUrlOrThrow()) }
    }
}

data class FastForwardModifyOutput(override val url: StdUrl) : StepResult
