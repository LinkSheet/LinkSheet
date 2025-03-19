package fe.linksheet.experiment.engine


interface PipelineHook {

}

interface BeforeStepHook : PipelineHook {
    fun <Result : StepResult> onBeforeRun(step: PipelineStep<Result>, url: String)
}

interface AfterStepHook : PipelineHook {
    fun <Result : StepResult> onAfterRun(step: PipelineStep<Result>, url: String, result: Result?)
}
