package fe.linksheet.experiment.engine


interface PipelineHook {

}

interface BeforeStepHook : PipelineHook {
    fun <R : StepResult> onBeforeRun(step: EngineStep<R>, url: String)
}

interface AfterStepHook : PipelineHook {
    fun <R : StepResult> onAfterRun(step: EngineStep<R>, url: String, result: R?)
}
