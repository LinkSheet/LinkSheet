package fe.linksheet.eval.rule

import fe.linksheet.eval.EvalContextImpl
import fe.linksheet.eval.KnownTokens
import fe.linksheet.eval.expression.Expression
import fe.linksheet.eval.expression.toInput
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.rule.PostProcessorInput
import fe.linksheet.experiment.engine.rule.PostprocessorRule

class ExpressionPostProcessorRule(val expression: Expression<*>) : PostprocessorRule {
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
