package fe.linksheet.experiment.engine.rule

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.slot.AppRoleId
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.util.AndroidAppPackage
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class DefaultBrowserRuleTest : BaseRuleEngineTest() {
    private val dispatcher = StandardTestDispatcher()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/415
    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/591
    private val rule = object : PostprocessorRule {
        private val defaultBrowsers = mapOf(
            "google.com" to AndroidAppPackage("com.google.chrome"),
            "github.com" to AndroidAppPackage("org.mozilla.fennec_fdroid")
        )

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val url = input.resultUrl
            val appPackage = defaultBrowsers[url.host]
            if (appPackage != null) {
                roles[AppRoleId.Browser] = appPackage
            }

            return empty()
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

    @JunitTest
    fun `test rule matched`() = runTest(dispatcher) {
        val result = engine.process("https://github.com/LinkSheet/LinkSheet".toStdUrlOrThrow())
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .transform { it.toString() }
            .isEqualTo("https://github.com/LinkSheet/LinkSheet")

        assertContext(result)
            .transform { it.roles[AppRoleId.Browser]?.packageName }
            .isEqualTo("org.mozilla.fennec_fdroid")
    }
}
