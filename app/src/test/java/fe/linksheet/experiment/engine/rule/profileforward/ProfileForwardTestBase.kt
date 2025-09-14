package fe.linksheet.experiment.engine.rule.profileforward

import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.ForwardOtherProfileResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.rule.LazyTestLinkEngine
import fe.linksheet.experiment.engine.rule.PreprocessorRule
import fe.linksheet.experiment.engine.rule.StepTestResult
import fe.linksheet.experiment.engine.rule.TestLinkModifier
import fe.linksheet.experiment.engine.rule.assertResult
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.getValue

class ProfileForwardTestBase(dispatcher: CoroutineDispatcher, rule: PreprocessorRule) {
    private val engine by LazyTestLinkEngine(dispatcher, rule)

    suspend fun `test rule matched`() {
        val result = engine.process("https://sso.mycompany.com/login?foo=bar".toStdUrlOrThrow())
        assertResult(result).isInstanceOf<ForwardOtherProfileResult>()
    }
}
