package fe.linksheet.experiment.engine.rule

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext

interface Rule<in Input : RuleInput, out Result : EngineResult> {
    suspend fun EngineRunContext.checkRule(input: Input): Result?
}

interface RuleInput
