package app.linksheet.feature.engine.core.rule.extra

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PreProcessorInput
import app.linksheet.feature.engine.core.rule.PreProcessorRule
import app.linksheet.feature.engine.eval.EvalContextImpl
import app.linksheet.feature.engine.eval.expression.AddFlagExpression
import app.linksheet.feature.engine.eval.expression.GetSourceAppExtraExpression
import fe.linksheet.testlib.core.BaseUnitTest
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.eval.expression.StringEqualsExpression
import app.linksheet.feature.engine.eval.expression.toInput
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
