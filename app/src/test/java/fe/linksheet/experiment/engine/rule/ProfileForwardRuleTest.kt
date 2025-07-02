package fe.linksheet.experiment.engine.rule

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.*
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ProfileForwardRuleTest : BaseUnitTest  {
    private val dispatcher = StandardTestDispatcher()

    private val rule = object : PreprocessorRule {
        private val workRelatedHost = "sso.mycompany.com"
        override suspend fun EngineRunContext.checkRule(input: PreProcessorInput): EngineResult? {
            if (input.url.host == workRelatedHost) {
                return ForwardOtherProfileResult(input.url)
            }

            return null
        }
    }

    private val engine = LinkEngine(
        steps = listOf(
            TestLinkModifier(EngineStepId.Embed),
            TestLinkModifier(EngineStepId.ClearURLs) { StepTestResult(it) }
        ),
        rules = listOf(rule),
        dispatcher = dispatcher,
    )

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://sso.mycompany.com/login?foo=bar".toStdUrlOrThrow())
        assertResult(result).isInstanceOf<ForwardOtherProfileResult>()
    }
}
