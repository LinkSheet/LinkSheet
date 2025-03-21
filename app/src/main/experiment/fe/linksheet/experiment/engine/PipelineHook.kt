package fe.linksheet.experiment.engine


interface PipelineHook {

}

interface BeforeStepHook : PipelineHook {
    fun <R : StepResult> onBeforeRun(step: PipelineStep<R>, url: String)
}

interface AfterStepHook : PipelineHook {
    fun <R : StepResult> onAfterRun(step: PipelineStep<R>, url: String, result: R?)
}
