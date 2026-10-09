package app.linksheet.feature.engine.core.rule.urlrewrite

import app.linksheet.feature.engine.core.UrlEngineResult
import app.linksheet.feature.engine.core.rule.LazyTestLinkEngine
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import app.linksheet.feature.engine.core.rule.assertResult
import app.linksheet.feature.engine.core.rule.processTest
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher

class UrlRewriteTestBase(dispatcher: CoroutineDispatcher, rule: PostProcessorRule) {
    private val engine by LazyTestLinkEngine(dispatcher, rule)

    suspend fun `test rule not matched`() {
        val result = engine.processTest(
            url = "https://developer.android.com/jetpack/androidx/releases/compose-material3".toStdUrlOrThrow()
        )
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://developer.android.com/jetpack/androidx/releases/compose-material3")
    }

    suspend fun `test rule matched`() {
        val result = engine.processTest(
            "https://www.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/".toStdUrlOrThrow()
        )
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://old.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/")
    }
}
