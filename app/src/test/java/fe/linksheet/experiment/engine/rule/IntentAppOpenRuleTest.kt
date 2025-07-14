package fe.linksheet.experiment.engine.rule

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.composekit.intent.buildIntent
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.IntentEngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.extension.std.toAndroidUri
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class IntentAppOpenRuleTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

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
        dispatcher = dispatcher,
    )

    @org.junit.Test
    fun `test rule not matched`() = runTest(dispatcher) {
        val result = engine.process("https://linksheet.app".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://linksheet.app")
    }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://linksheet.app/fakevideo.mp4".toStdUrlOrThrow())

        assertResult(result)
            .isInstanceOf<IntentEngineResult>()
            .transform { it.intent }
            .prop(Intent::getDataString)
            .isEqualTo("https://linksheet.app/fakevideo.mp4")
    }
}
