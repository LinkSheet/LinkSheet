package fe.linksheet.experiment.engine.rule.urlrewrite

import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.rule.LazyTestLinkEngine
import fe.linksheet.experiment.engine.rule.PostprocessorRule
import fe.linksheet.experiment.engine.rule.assertResult
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher

class UrlRewriteTestBase(dispatcher: CoroutineDispatcher, rule: PostprocessorRule) {
    private val engine by LazyTestLinkEngine(dispatcher, rule)

    suspend fun `test rule not matched`() {
        val result = engine.process(
            "https://developer.android.com/jetpack/androidx/releases/compose-material3".toStdUrlOrThrow()
        )
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://developer.android.com/jetpack/androidx/releases/compose-material3")
    }

    suspend fun `test rule matched`() {
        val result = engine.process(
            "https://www.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/".toStdUrlOrThrow()
        )
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://old.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/")
    }
}
