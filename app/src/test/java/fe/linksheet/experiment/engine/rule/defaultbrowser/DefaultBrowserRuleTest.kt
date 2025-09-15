package fe.linksheet.experiment.engine.rule.defaultbrowser

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.AppRoleId
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.rule.PostProcessorInput
import fe.linksheet.experiment.engine.rule.PostProcessorRule
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.AndroidAppPackage
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class DefaultBrowserRuleTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/415
    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/591
    private val rule = object : PostProcessorRule {
        private val defaultBrowsers = mapOf(
            "google.com" to AndroidAppPackage("com.google.chrome"),
            "github.com" to AndroidAppPackage("org.mozilla.fennec_fdroid")
        )

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val url = input.resultUrl
            val appPackage = defaultBrowsers[url.host]
            if (appPackage != null) {
                put(AppRoleId.Browser, appPackage)
            }

            return empty()
        }
    }
    private val base by lazy { DefaultBrowserTestBase(dispatcher, rule) }

    @Test
    fun `test default browser 1`() = runTest(dispatcher) {
        base.`test default browser 1`()
    }

    @Test
    fun `test default browser 2`() = runTest(dispatcher) {
        base.`test default browser 2`()
    }

    @Test
    fun `test no default browser`() = runTest(dispatcher) {
        base.`test no default browser`()
    }
}
