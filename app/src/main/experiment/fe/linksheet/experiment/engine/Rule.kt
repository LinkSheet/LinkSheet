package fe.linksheet.experiment.engine

import java.util.EnumSet

interface Rule<in Input : RuleInput, out Result : EngineResult> {
    suspend fun EngineRunContext.checkRule(input: Input): Result?
}

sealed interface RuleInput
data class PreProcessorInput(val url: String) : RuleInput

data class PostProcessorInput(val resultUrl: String, val originalUrl: String) : RuleInput

interface PreprocessorRule : Rule<PreProcessorInput, EngineResult> {

}

interface PostprocessorRule : Rule<PostProcessorInput, EngineResult> {

}

interface StepRule<in Input : RuleInput, out Result : StepRuleResult> : Rule<Input, Result> {
    val steps: EnumSet<EngineStepId>
}

sealed interface StepRuleInput : RuleInput

data class StepStart<T : StepResult>(val depth: Int, val step: EngineStep<T>, val url: String) : StepRuleInput
data class StepEnd<T : StepResult>(
    val depth: Int,
    val step: EngineStep<T>,
    val url: String,
    val hasNewUrl: Boolean,
    val resultUrl: String,
) : StepRuleInput

interface BeforeStepRule : StepRule<StepStart<*>, StepRuleResult> {

}

interface AfterStepRule : StepRule<StepEnd<*>, StepRuleResult> {

}


sealed interface StepRuleResult : EngineResult {

}

data object None : StepRuleResult
data object Terminate : StepRuleResult
data object SkipStep : StepRuleResult
