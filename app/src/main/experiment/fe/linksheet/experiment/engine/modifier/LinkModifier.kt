package fe.linksheet.experiment.engine.modifier

import fe.linksheet.experiment.engine.EngineStep
import fe.linksheet.experiment.engine.StepResult

sealed interface LinkModifier<out Result : StepResult> : EngineStep<Result> {
   suspend fun warmup()
}

