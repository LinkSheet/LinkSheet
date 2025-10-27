package app.linksheet.feature.engine.core.rule.intentappopen

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.composekit.intent.buildIntent
import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.IntentEngineResult
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PostProcessorInput
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import fe.linksheet.extension.toAndroidUri
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class IntentAppOpenRuleTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    // Use case/FR: https://github.com/LinkSheet/LinkSheet/issues/428
    private val rule = object : PostProcessorRule {
        private val regex = """https?://.*\.(mp3|wav|flac|m4a|aac|oog|mp4)(/.*|\?.*)?$""".toRegex()
        private val cmp = ComponentName("com.dv.adm", "com.dv.adm.AEditor")

        override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
            val match = regex.matchEntire(input.resultUrl.toString()) ?: return null

            val uri = input.resultUrl.toAndroidUri()
            val baseIntent = buildIntent(Intent.ACTION_VIEW, uri, cmp)
            return IntentEngineResult(baseIntent)
        }
    }

    private val base by lazy { IntentAppOpenTestBase(dispatcher, rule) }

    @org.junit.Test
    fun `test rule not matched`() = runTest(dispatcher) {
        base.`test rule not matched`()
    }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.`test rule matched`()
    }
}
