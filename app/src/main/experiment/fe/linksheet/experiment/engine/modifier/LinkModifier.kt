package fe.linksheet.experiment.engine.modifier

import fe.linksheet.experiment.engine.PipelineStep
import fe.linksheet.experiment.engine.StepResult

sealed interface LinkModifier<out Result : StepResult> : PipelineStep<Result> {
   suspend fun warmup()
}

