package app.linksheet.feature.engine.core.rule.defaultbrowser

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.AppRoleId
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PostProcessorInput
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import app.linksheet.feature.engine.eval.EvalContextImpl
import app.linksheet.feature.engine.eval.KnownTokens
import app.linksheet.feature.engine.eval.expression.BlockExpression
import app.linksheet.feature.engine.eval.expression.Component
import app.linksheet.feature.engine.eval.expression.ConstantExpression
import app.linksheet.feature.engine.eval.expression.Expression
import app.linksheet.feature.engine.eval.expression.IfExpression
import app.linksheet.feature.engine.eval.expression.PutAppRoleExpression
import app.linksheet.feature.engine.eval.expression.StringEqualsExpression
import app.linksheet.feature.engine.eval.expression.UrlGetComponentExpression
import app.linksheet.feature.engine.eval.expression.toInput
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DefaultBrowserRuleEvalTest : BaseUnitTest {
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
    private val expression by lazy {
        val host = UrlGetComponentExpression(
            expression = KnownTokens.ResultUrl,
            component = ConstantExpression(Component.Host)
        )
        BlockExpression(
            expressions = listOf(
                IfExpression(
                    condition = StringEqualsExpression(
                        left = host as Expression<String?>,
                        right = ConstantExpression("google.com"),
                        ignoreCase = true
                    ),
                    body = PutAppRoleExpression(
                        expression = KnownTokens.EngineRunContext,
                        id = ConstantExpression(AppRoleId.Browser),
                        packageName = ConstantExpression("com.google.chrome")
                    )
                ),
                IfExpression(
                    condition = StringEqualsExpression(
                        left = host as Expression<String?>,
                        right = ConstantExpression("github.com"),
                        ignoreCase = true
                    ),
                    body = PutAppRoleExpression(
                        expression = KnownTokens.EngineRunContext,
                        id = ConstantExpression(AppRoleId.Browser),
                        packageName = ConstantExpression("org.mozilla.fennec_fdroid")
                    )
                )
            )
        )
    }

    private val base by lazy { DefaultBrowserTestBase(dispatcher, ExpressionPostprocessorRule(expression)) }

    @Test
    fun `test default browser 1`() = runTest(dispatcher) {
        base.`test default browser 1`()
    }

    @Test
    fun `test default browser 2`() = runTest(dispatcher) {
        base.`test default browser 2`()
    }

    @Test
    fun `test no default browser`() = runTest(dispatcher) {
        base.`test no default browser`()
    }
}
