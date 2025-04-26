package fe.linksheet.experiment.engine

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.std.uri.extension.new
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class LinkEngineUrlRewriteRuleTest : BaseLinkEngineTest() {
    private val dispatcher = StandardTestDispatcher()
    private val testPrintLogger = createTestEngineLogger<LinkEnginePostProcessorRuleTest>()

    private val hosts = setOf("reddit.com", "www.reddit.com")
    private val newHost = "old.reddit.com"

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/407
    private val rule = object : PostprocessorRule {
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
        logger = testPrintLogger,
        dispatcher = dispatcher,
    )

    @Test
    fun `test rule not matched`() = runTest(dispatcher) {
        val result = engine.process("https://developer.android.com/jetpack/androidx/releases/compose-material3".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://developer.android.com/jetpack/androidx/releases/compose-material3")
    }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://www.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://old.reddit.com/r/androiddev/comments/1k69xx8/jetpack_compose_180_is_now_stable/")
    }
}
