package fe.linksheet.experiment.engine.rule

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.*
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class ProfileForwardRuleTest : BaseRuleEngineTest() {
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

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://sso.mycompany.com/login?foo=bar".toStdUrlOrThrow())
        assertResult(result).isInstanceOf<ForwardOtherProfileResult>()
    }
}
