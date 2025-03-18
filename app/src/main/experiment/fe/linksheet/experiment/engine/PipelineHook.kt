package fe.linksheet.experiment.engine


interface PipelineHook {

}

interface BeforeStepHook : PipelineHook {
    fun <Result : StepResult> onBeforeRun(step: PipelineStep<Result>, mutUrl: String)
}

interface AfterStepHook : PipelineHook {
    fun <Result : StepResult> onAfterRun(step: PipelineStep<Result>, mutUrl: String, result: Result?)
}
