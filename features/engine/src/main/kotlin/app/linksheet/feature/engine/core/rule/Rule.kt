package app.linksheet.feature.engine.core.rule

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.EngineRunContext


interface Rule<in Input : RuleInput, out Result : EngineResult> {
    suspend fun EngineRunContext.checkRule(input: Input): Result?
}

interface RuleInput
