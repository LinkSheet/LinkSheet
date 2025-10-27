package app.linksheet.feature.engine.core.rule.defaultbrowser

import app.linksheet.feature.engine.core.ContextualEngineResult
import app.linksheet.feature.engine.core.UrlEngineResult
import app.linksheet.feature.engine.core.context.AppRoleId
import app.linksheet.feature.engine.core.context.SealedRunContext
import app.linksheet.feature.engine.core.context.findRoleOrNull
import app.linksheet.feature.engine.core.rule.LazyTestLinkEngine
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import app.linksheet.feature.engine.core.rule.assertContext
import app.linksheet.feature.engine.core.rule.assertResult
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.CoroutineDispatcher

class DefaultBrowserTestBase(dispatcher: CoroutineDispatcher, rule: PostProcessorRule) {
    private val engine by LazyTestLinkEngine(dispatcher, rule)

    suspend fun `test default browser 1`() {
        val result = baseTest("https://github.com/LinkSheet/LinkSheet".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.findSingleRole() }
            .isEqualTo("org.mozilla.fennec_fdroid")
    }

    suspend fun `test default browser 2`() {
        val result = baseTest("https://google.com/hello".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.findSingleRole() }
            .isEqualTo("com.google.chrome")
    }

    suspend fun `test no default browser`() {
        val result = baseTest("https://linksheet.app".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.findSingleRole() }
            .isNull()
    }

    private suspend fun baseTest(url: StdUrl): ContextualEngineResult {
        val result = engine.process(url)
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .isEqualTo(url)

        return result
    }

    private fun SealedRunContext.findSingleRole(id: AppRoleId = AppRoleId.Browser): String? {
        return roles.findRoleOrNull(id).singleOrNull()?.packageName
    }
}
