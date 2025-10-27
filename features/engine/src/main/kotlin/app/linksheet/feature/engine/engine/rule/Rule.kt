package app.linksheet.feature.engine.engine.rule

import app.linksheet.feature.engine.engine.EngineResult
import app.linksheet.feature.engine.engine.context.EngineRunContext


interface Rule<in Input : RuleInput, out Result : EngineResult> {
    suspend fun EngineRunContext.checkRule(input: Input): Result?
}

interface RuleInput
