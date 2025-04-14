package fe.linksheet.experiment.engine

interface EngineStep<out R : StepResult> {
    val id: EngineStepId

    suspend fun EngineRunContext.runStep(url: String): R?
}

interface InPlaceStep

interface StepResult {
    val url: String
}

enum class EngineStepId {
    ClearURLs,
    Embed,
    FastForward,
    LibRedirect,
    Amp2Html,
    FollowRedirects
}
