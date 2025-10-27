package app.linksheet.feature.engine.eval.rule

import app.linksheet.feature.engine.engine.EngineResult
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.rule.PostProcessorInput
import app.linksheet.feature.engine.engine.rule.PostProcessorRule
import app.linksheet.feature.engine.eval.EvalContextImpl
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.toInput


class ExpressionPostProcessorRule(val expression: Expression<*>) : PostProcessorRule {
    override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
        val ctx = EvalContextImpl(
            KnownTokens.EngineRunContext.toInput(this),
            KnownTokens.OriginalUrl.toInput(input.originalUrl),
            KnownTokens.ResultUrl.toInput(input.resultUrl),
        )

        val result = expression.eval(ctx)
        if (result is EngineResult) return result
        return empty()
    }
}
