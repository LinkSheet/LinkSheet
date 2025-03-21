package fe.linksheet.experiment.engine

interface PipelineStep<out R : StepResult>{
    suspend fun run(url: String): R?
}

interface InPlaceStep

interface StepResult {
    val url: String
}
