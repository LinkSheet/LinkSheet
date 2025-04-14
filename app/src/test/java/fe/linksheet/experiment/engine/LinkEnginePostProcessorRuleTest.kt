package fe.linksheet.experiment.engine

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.util.intent.buildIntent
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class LinkEnginePostProcessorRuleTest : BaseLinkEngineTest() {
    private val dispatcher = StandardTestDispatcher()
    private val testPrintLogger = createTestEngineLogger<LinkEnginePostProcessorRuleTest>()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/428
    private val rule = object : PostprocessorRule {
        private val regex = """https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""".toRegex()
        private val cmp = ComponentName("com.dv.adm", "com.dv.adm.AEditor")

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val match = regex.matchEntire(input.resultUrl)
            if (match == null) return null

            val uri = Uri.parse(input.resultUrl)
            val baseIntent = buildIntent(Intent.ACTION_VIEW, uri, cmp)
            return IntentEngineResult(baseIntent)
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
        val result = engine.process("https://linksheet.app")
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .isEqualTo("https://linksheet.app")
    }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://linksheet.app/fakevideo.mp4")

        assertResult(result)
            .isInstanceOf<IntentEngineResult>()
            .transform { it.intent }
            .prop(Intent::getDataString)
            .isEqualTo("https://linksheet.app/fakevideo.mp4")
    }
}
