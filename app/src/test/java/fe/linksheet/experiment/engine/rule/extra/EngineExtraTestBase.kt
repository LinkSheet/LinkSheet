package fe.linksheet.experiment.engine.rule.extra

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.EngineFlag
import fe.linksheet.experiment.engine.context.SourceAppExtra
import fe.linksheet.experiment.engine.rule.LazyTestLinkEngine
import fe.linksheet.experiment.engine.rule.PreprocessorRule
import fe.linksheet.experiment.engine.rule.assertResult
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher

class EngineExtraTestBase(dispatcher: CoroutineDispatcher, rule: PreprocessorRule) {
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
