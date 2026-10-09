@file:OptIn(ExperimentalSerializationApi::class)

package app.linksheet.feature.engine.core.rule.urlrewrite

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PostProcessorInput
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.EvalContextImpl
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.Component
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.eval.expression.OrExpression
import app.linksheet.feature.engine.eval.expression.StringEqualsExpression
import app.linksheet.feature.engine.eval.expression.UrlEngineResultExpression
import app.linksheet.feature.engine.eval.expression.UrlGetComponentExpression
import app.linksheet.feature.engine.eval.expression.UrlSetComponentExpression
import app.linksheet.feature.engine.eval.expression.toInput
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import org.junit.Test

internal class UrlRewriteRuleEvalTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    class ExpressionPostprocessorRule(val expression: Expression<*>) : PostProcessorRule {
        context(context: EngineRunContext)
        override suspend fun checkRule(input: PostProcessorInput): EngineResult? {
            val ctx = EvalContextImpl(
                KnownTokens.EngineRunContext.toInput(context),
                KnownTokens.ResultUrl.toInput(input.resultUrl)
            )

            val result = expression.eval(ctx)
            if (result is EngineResult) return result
            return context.empty()
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
