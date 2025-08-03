package fe.linksheet.experiment.engine.step

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.std.uri.StdUrl

interface EngineStep<out R : StepResult> {
    val enabled: () -> Boolean

    val id: EngineStepId

    suspend fun EngineRunContext.runStep(url: StdUrl): R?
}

interface InPlaceStep

interface StepResult {
    val url: StdUrl
}

enum class EngineStepId {
    ClearURLs,
    Embed,
    FastForward,
    LibRedirect,
    Amp2Html,
    FollowRedirects
}
