package app.linksheet.feature.engine.eval.rule

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PreProcessorInput
import app.linksheet.feature.engine.core.rule.PreProcessorRule
import app.linksheet.feature.engine.eval.EvalContextImpl
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.toInput

class ExpressionPreProcessorRule(val expression: Expression<*>) : PreProcessorRule {
    context(context: EngineRunContext)
    override suspend fun checkRule(input: PreProcessorInput): EngineResult? {
        val ctx = EvalContextImpl(
            KnownTokens.EngineRunContext.toInput(context),
            KnownTokens.OriginalUrl.toInput(input.url)
        )

        val result = expression.eval(ctx)
        if (result is EngineResult) return result
        return context.empty()
    }
}
