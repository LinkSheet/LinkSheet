package fe.linksheet.experiment.engine

interface PipelineStep<out Result : StepResult>{
    suspend fun run(url: String): Result?
}

interface InPlaceStep

interface StepResult {
    val url: String
}
