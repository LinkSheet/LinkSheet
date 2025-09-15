package fe.linksheet.experiment.engine.rule.intentappopen

import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.eval.KnownTokens
import fe.linksheet.eval.BundleSerializer
import fe.linksheet.eval.expression.*
import fe.linksheet.eval.rule.ExpressionPostProcessorRule
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class IntentAppOpenRuleEvalTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    private val expression by lazy {
        IfExpression(
            condition = RegexMatchEntireExpression(
                regex = RegexExpression(
                    expression = ConstantExpression("""https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""")
                ),
                string = UrlStringExpression(
                    expression = KnownTokens.ResultUrl,
                )
            ),
            body = IntentEngineResultExpression(
                expression = IntentComponentNameExpression(
                    action = ConstantExpression(Intent.ACTION_VIEW),
                    data = UrlToAndroidUriExpression(
                        expression = KnownTokens.ResultUrl
                    ),
                    componentName = ComponentNameExpression(
                        pkg = ConstantExpression("com.dv.adm"),
                        cls = ConstantExpression("com.dv.adm.AEditor")
                    )
                )
            )
        )
    }

    private val base by lazy {
        IntentAppOpenTestBase(dispatcher, ExpressionPostProcessorRule(expression))
    }

    @Test
    fun `test rule not matched`() = runTest(dispatcher) {
        base.`test rule not matched`()
    }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.`test rule matched`()
    }

    @Test
    fun test() {
        val x = BundleSerializer.encodeToHexString(1, expression)
        println(BundleSerializer.decodeFromHexString(x))
    }
}
