package fe.linksheet.experiment.engine.rule.urlrewrite

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.UrlEngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.rule.PostProcessorInput
import fe.linksheet.experiment.engine.rule.PostprocessorRule
import fe.linksheet.experiment.engine.rule.urlrewrite.UrlRewriteTestBase
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.extension.new
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

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

    private val base by lazy { UrlRewriteTestBase(dispatcher, rule) }

    @org.junit.Test
    fun `test rule not matched`() = runTest(dispatcher) {
        base.`test rule not matched`()
    }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.`test rule matched`()
    }
}
