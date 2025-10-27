package app.linksheet.feature.engine.engine.modifier

import app.linksheet.feature.engine.engine.step.EngineStep
import app.linksheet.feature.engine.engine.step.StepResult

interface LinkModifier<out Result : StepResult> : EngineStep<Result> {
   suspend fun warmup()
}

