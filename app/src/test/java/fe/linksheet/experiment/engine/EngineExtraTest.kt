package fe.linksheet.experiment.engine

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isInstanceOf
import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.EngineFlag
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.SourceAppExtra
import fe.linksheet.experiment.engine.context.findExtraOrNull
import fe.linksheet.experiment.engine.rule.PreProcessorInput
import fe.linksheet.experiment.engine.rule.PreprocessorRule
import fe.linksheet.experiment.engine.rule.StepTestResult
import fe.linksheet.experiment.engine.rule.TestLinkModifier
import fe.linksheet.experiment.engine.rule.assertResult
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.AndroidAppPackage
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf


//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class EngineExtraTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    private val rule = object : PreprocessorRule {
        private val chromePackage = AndroidAppPackage("com.google.chrome")

        override suspend fun EngineRunContext.checkRule(input: PreProcessorInput): EngineResult? {
            val extra = findExtraOrNull<SourceAppExtra>()
            if (extra?.appPackage == chromePackage.packageName) {
                flags.add(EngineFlag.DisablePreview)
            }

            return empty()
        }
    }

    private val engine by lazy {
        LinkEngine(
            steps = listOf(
                TestLinkModifier(EngineStepId.Embed),
                TestLinkModifier(EngineStepId.ClearURLs) { StepTestResult(it) }
            ),
            rules = listOf(rule),
            dispatcher = dispatcher,
        )
    }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        val url = "https://linksheet.app".toStdUrlOrThrow()
        val extra = SourceAppExtra("com.google.chrome")
        val context = DefaultEngineRunContext(extra)

        val result = engine.process(url, context)
        assertResult(result).isInstanceOf<UrlEngineResult>()
        assertThat(context.flags).containsExactlyInAnyOrder(EngineFlag.DisablePreview)
    }
}
