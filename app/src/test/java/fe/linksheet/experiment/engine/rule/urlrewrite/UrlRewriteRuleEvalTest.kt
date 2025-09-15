@file:OptIn(ExperimentalSerializationApi::class)

package fe.linksheet.experiment.engine.rule.urlrewrite

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.rule.PostProcessorInput
import fe.linksheet.experiment.engine.rule.PostProcessorRule
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.eval.EvalContextImpl
import fe.linksheet.eval.BundleSerializer
import fe.linksheet.eval.KnownTokens
import fe.linksheet.eval.expression.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Test

internal class UrlRewriteRuleEvalTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    class ExpressionPostprocessorRule(val expression: Expression<*>) : PostProcessorRule {
        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val ctx = EvalContextImpl(
                KnownTokens.EngineRunContext.toInput(this),
                KnownTokens.ResultUrl.toInput(input.resultUrl)
            )

            val result = expression.eval(ctx)
            if (result is EngineResult) return result
            return empty()
        }
    }

    private val expression = run {
        val hostComponent = ConstantExpression(Component.Host)
        val host = UrlGetComponentExpression(
            expression = KnownTokens.ResultUrl,
            component = hostComponent
        )

        IfExpression(
            condition = OrExpression(
                left = StringEqualsExpression(
                    left = host as Expression<String?>,
                    right = ConstantExpression("reddit.com"),
                    ignoreCase = true
                ),
                right = StringEqualsExpression(
                    left = host as Expression<String?>,
                    right = ConstantExpression("www.reddit.com"),
                    ignoreCase = true
                )
            ),
            body = UrlEngineResultExpression(
                expression = UrlSetComponentExpression(
                    expression = KnownTokens.ResultUrl,
                    component = hostComponent,
                    value = ConstantExpression("old.reddit.com")
                )
            )
        )
    }

    private val base by lazy { UrlRewriteTestBase(dispatcher, ExpressionPostprocessorRule(expression)) }

    @Test
    fun `test rule not matched`() = runTest(dispatcher) {
        base.`test rule not matched`()
    }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.`test rule matched`()
    }

    @Test
    fun printExpression() {
        val hex = BundleSerializer.encodeToHexString(version = 1, expression = expression)
        println(hex)
    }
}
