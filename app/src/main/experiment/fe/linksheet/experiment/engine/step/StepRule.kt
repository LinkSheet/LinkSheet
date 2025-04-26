package fe.linksheet.experiment.engine.step

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.rule.Rule
import fe.linksheet.experiment.engine.rule.RuleInput
import fe.std.uri.StdUrl
import java.util.EnumSet

interface StepRule<in Input : RuleInput, out Result : StepRuleResult> : Rule<Input, Result> {
    val steps: EnumSet<EngineStepId>
}

sealed interface StepRuleInput : RuleInput

data class StepStart<T : StepResult>(val depth: Int, val step: EngineStep<T>, val url: StdUrl) : StepRuleInput
data class StepEnd<T : StepResult>(
    val depth: Int,
    val step: EngineStep<T>,
    val url: StdUrl,
    val hasNewUrl: Boolean,
    val resultUrl: StdUrl,
) : StepRuleInput

interface BeforeStepRule : StepRule<StepStart<*>, StepRuleResult>
interface AfterStepRule : StepRule<StepEnd<*>, StepRuleResult>

sealed interface StepRuleResult : EngineResult
data object None : StepRuleResult
data object Terminate : StepRuleResult
data object SkipStep : StepRuleResult
