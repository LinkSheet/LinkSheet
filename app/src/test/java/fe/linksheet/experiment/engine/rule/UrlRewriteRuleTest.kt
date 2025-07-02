package fe.linksheet.experiment.engine.rule

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.extension.new
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class UrlRewriteRuleTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/407
    private val rule = object : PostprocessorRule {
        private val hosts = setOf("reddit.com", "www.reddit.com")
        private val newHost = "old.reddit.com"

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val url = input.resultUrl
            if (url.host !in hosts) return UrlEngineResult(url)

            val newUrl = url.new { setHost(newHost) }
            return UrlEngineResult(newUrl)
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
    fun `test rule not matched`() = runTest(dispatcher) {
        val result =
            engine.process("https://developer.android.com/jetpack/androidx/releases/compose-material3".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://developer.android.com/jetpack/androidx/releases/compose-material3")
    }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result =
            engine.process("https://www.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://old.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/")
    }
}
