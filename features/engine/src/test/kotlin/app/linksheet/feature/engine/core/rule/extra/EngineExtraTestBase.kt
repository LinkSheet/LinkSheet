package app.linksheet.feature.engine.core.rule.extra

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isInstanceOf
import app.linksheet.feature.engine.core.UrlEngineResult
import app.linksheet.feature.engine.core.context.DefaultEngineRunContext
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.core.context.SourceAppExtra
import app.linksheet.feature.engine.core.rule.LazyTestLinkEngine
import app.linksheet.feature.engine.core.rule.PreProcessorRule
import app.linksheet.feature.engine.core.rule.assertResult
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher

class EngineExtraTestBase(dispatcher: CoroutineDispatcher, rule: PreProcessorRule) {
    private val engine by  LazyTestLinkEngine(dispatcher, rule)

    suspend fun test() {
        val url = "https://linksheet.app".toStdUrlOrThrow()
        val extra = SourceAppExtra("com.google.chrome")
        val context = DefaultEngineRunContext(extra)

        val result = engine.process(url, context)

        assertResult(result).isInstanceOf<UrlEngineResult>()
        assertThat(context.flags).containsExactlyInAnyOrder(EngineFlag.DisablePreview)
    }
}
