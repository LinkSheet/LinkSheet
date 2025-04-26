package fe.linksheet.experiment.engine.modifier

import fe.linksheet.experiment.engine.step.EngineStep
import fe.linksheet.experiment.engine.step.StepResult

interface LinkModifier<out Result : StepResult> : EngineStep<Result> {
   suspend fun warmup()
}

