package fe.linksheet.experiment.engine.rule.extra

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineFlag
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.rule.PreProcessorInput
import fe.linksheet.experiment.engine.rule.PreProcessorRule
import fe.linksheet.eval.expression.AddFlagExpression
import fe.linksheet.eval.expression.GetSourceAppExtraExpression
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.eval.EvalContextImpl
import fe.linksheet.eval.KnownTokens
import fe.linksheet.eval.expression.ConstantExpression
import fe.linksheet.eval.expression.Expression
import fe.linksheet.eval.expression.IfExpression
import fe.linksheet.eval.expression.StringEqualsExpression
import fe.linksheet.eval.expression.toInput
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class EngineExtraEvalTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    class ExpressionPreprocessorRule(val expression: Expression<*>) : PreProcessorRule {
        override suspend fun EngineRunContext.checkRule(input: PreProcessorInput): EngineResult? {
            val ctx = EvalContextImpl(
                KnownTokens.EngineRunContext.toInput(this),
                KnownTokens.OriginalUrl.toInput(input.url)
            )

            val result = expression.eval(ctx)
            if (result is EngineResult) return result
            return empty()
        }
    }

    private val expression by lazy {
        IfExpression(
            condition = StringEqualsExpression(
                left = GetSourceAppExtraExpression(expression = KnownTokens.EngineRunContext),
                right = ConstantExpression("com.google.chrome"),
                ignoreCase = true
            ),
            body = AddFlagExpression(
                expression = KnownTokens.EngineRunContext,
                flag = ConstantExpression(EngineFlag.DisablePreview)
            )
        )
    }

    private val base by lazy {
        EngineExtraTestBase(dispatcher, ExpressionPreprocessorRule(expression))
    }

    @Test
    fun test() = runTest(dispatcher) {
        base.test()
    }
}
