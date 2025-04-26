package fe.linksheet.experiment.engine.rule

import android.content.ComponentName
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.IntentEngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.extension.std.toAndroidUri
import fe.linksheet.util.intent.buildIntent
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class IntentAppOpenRuleTest : BaseRuleEngineTest() {
    private val dispatcher = StandardTestDispatcher()
    private val testPrintLogger = createTestEngineLogger<IntentAppOpenRuleTest>()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/428
    private val rule = object : PostprocessorRule {
        private val regex = """https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""".toRegex()
        private val cmp = ComponentName("com.dv.adm", "com.dv.adm.AEditor")

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val match = regex.matchEntire(input.resultUrl.toString())
            if (match == null) return null

            val uri = input.resultUrl.toAndroidUri()
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
        val result = engine.process("https://linksheet.app".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://linksheet.app")
    }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://linksheet.app/fakevideo.mp4".toStdUrlOrThrow())

        assertResult(result)
            .isInstanceOf<IntentEngineResult>()
            .transform { it.intent }
            .prop(Intent::getDataString)
            .isEqualTo("https://linksheet.app/fakevideo.mp4")
    }
}
