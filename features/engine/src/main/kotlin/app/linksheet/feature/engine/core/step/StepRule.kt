package app.linksheet.feature.engine.core.step

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.rule.Rule
import app.linksheet.feature.engine.core.rule.RuleInput
import fe.std.uri.StdUrl
import java.util.EnumSet

interface StepRule<in Input : RuleInput, out Result : StepRuleResult> : Rule<Input, Result> {
    val steps: EnumSet<EngineStepId>
}

sealed interface StepRuleInput : RuleInput

data class StepStart<T : StepResult>(val depth: Int, val step: EngineStep<T>, val url: StdUrl) : StepRuleInput {
    override fun toString(): String {
        return "StepStart(depth=$depth, step=${step.id}, url=$url)"
    }
}

data class StepEnd<T : StepResult>(
    val depth: Int,
    val step: EngineStep<T>,
    val url: StdUrl,
    val hasNewUrl: Boolean,
    val resultUrl: StdUrl,
) : StepRuleInput {
    override fun toString(): String {
        return "StepEnd(depth=$depth, step=${step.id}, url=$url, hasNewUrl=$hasNewUrl, resultUrl=$resultUrl)"
    }
}

interface BeforeStepRule : StepRule<StepStart<*>, StepRuleResult>
interface AfterStepRule : StepRule<StepEnd<*>, StepRuleResult>

sealed interface StepRuleResult : EngineResult
data object None : StepRuleResult
data object Terminate : StepRuleResult
data object SkipStep : StepRuleResult
