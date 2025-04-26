package fe.linksheet.experiment.engine.resolver

import fe.linksheet.experiment.engine.step.EngineStep
import fe.linksheet.experiment.engine.step.StepResult
import fe.std.uri.StdUrl

interface LinkResolver : EngineStep<ResolveOutput> {
}


data class ResolveOutput(override val url: StdUrl) : StepResult
