package app.linksheet.feature.engine.core.resolver

import app.linksheet.feature.engine.core.step.EngineStep
import app.linksheet.feature.engine.core.step.StepResult
import fe.std.uri.StdUrl

interface LinkResolver : EngineStep<ResolveOutput> {
}


data class ResolveOutput(override val url: StdUrl) : StepResult
