package app.linksheet.feature.engine.core.rule.profileforward

import assertk.assertions.isInstanceOf
import app.linksheet.feature.engine.core.ForwardOtherProfileResult
import app.linksheet.feature.engine.core.rule.LazyTestLinkEngine
import app.linksheet.feature.engine.core.rule.PreProcessorRule
import app.linksheet.feature.engine.core.rule.assertResult
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
