package fe.linksheet.experiment.engine.rule.profileforward

import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.ForwardOtherProfileResult
import fe.linksheet.experiment.engine.rule.LazyTestLinkEngine
import fe.linksheet.experiment.engine.rule.PreProcessorRule
import fe.linksheet.experiment.engine.rule.assertResult
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.getValue

class ProfileForwardTestBase(dispatcher: CoroutineDispatcher, rule: PreProcessorRule) {
    private val engine by LazyTestLinkEngine(dispatcher, rule)

    suspend fun `test rule matched`() {
        val result = engine.process("https://sso.mycompany.com/login?foo=bar".toStdUrlOrThrow())
        assertResult(result).isInstanceOf<ForwardOtherProfileResult>()
    }
}
