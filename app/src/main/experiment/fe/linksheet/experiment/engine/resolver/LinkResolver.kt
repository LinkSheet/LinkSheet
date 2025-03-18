package fe.linksheet.experiment.engine.resolver

import fe.linksheet.experiment.engine.PipelineStep
import fe.linksheet.experiment.engine.StepResult

interface LinkResolver : PipelineStep<ResolveOutput> {
}


data class ResolveOutput(override val url: String) : StepResult



