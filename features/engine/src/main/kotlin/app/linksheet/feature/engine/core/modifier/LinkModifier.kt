package app.linksheet.feature.engine.core.modifier

import app.linksheet.feature.engine.core.step.EngineStep
import app.linksheet.feature.engine.core.step.StepResult

interface LinkModifier<out Result : StepResult> : EngineStep<Result> {
   suspend fun warmup()
}

