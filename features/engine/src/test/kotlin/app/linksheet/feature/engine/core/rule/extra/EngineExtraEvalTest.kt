package app.linksheet.feature.engine.core.rule.extra

import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.*
import app.linksheet.feature.engine.eval.rule.ExpressionPreProcessorRule
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class EngineExtraEvalTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

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
        EngineExtraTestBase(dispatcher, ExpressionPreProcessorRule(expression))
    }

    @Test
    fun test() = runTest(dispatcher) {
        base.test()
    }
}
