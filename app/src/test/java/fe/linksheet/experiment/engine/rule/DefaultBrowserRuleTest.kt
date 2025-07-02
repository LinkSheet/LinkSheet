package fe.linksheet.experiment.engine.rule

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import fe.linksheet.experiment.engine.ContextualEngineResult
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.slot.AppRoleId
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.AndroidAppPackage
import fe.std.uri.StdUrl
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DefaultBrowserRuleTest : BaseUnitTest {
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

    private suspend fun baseTest(url: StdUrl): ContextualEngineResult {
        val result = engine.process(url)
        assertResult(result)
            .isInstanceOf<UrlEngineResult>()
            .prop(UrlEngineResult::url)
            .isEqualTo(url)

        return result
    }

    @org.junit.Test
    fun `test default browser 1`() = runTest(dispatcher) {
        val result = baseTest("https://github.com/LinkSheet/LinkSheet".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.roles[AppRoleId.Browser]?.packageName }
            .isEqualTo("org.mozilla.fennec_fdroid")
    }

    @org.junit.Test
    fun `test default browser 2`() = runTest(dispatcher) {
        val result = baseTest("https://google.com/hello".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.roles[AppRoleId.Browser]?.packageName }
            .isEqualTo("com.google.chrome")
    }

    @org.junit.Test
    fun `test no default browser`() = runTest(dispatcher) {
        val result = baseTest("https://linksheet.app".toStdUrlOrThrow())

        assertContext(result)
            .transform { it.roles[AppRoleId.Browser]?.packageName }
            .isNull()
    }
}
