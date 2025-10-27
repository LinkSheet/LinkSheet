package app.linksheet.feature.engine.engine.resolver

import app.linksheet.feature.engine.engine.step.EngineStep
import app.linksheet.feature.engine.engine.step.StepResult
import fe.std.uri.StdUrl

interface LinkResolver : EngineStep<ResolveOutput> {
}


data class ResolveOutput(override val url: StdUrl) : StepResult
