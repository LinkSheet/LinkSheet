package fe.linksheet.experiment.engine.resolver

import fe.linksheet.experiment.engine.EngineStep
import fe.linksheet.experiment.engine.StepResult

interface LinkResolver : EngineStep<ResolveOutput> {
}


data class ResolveOutput(override val url: String) : StepResult
