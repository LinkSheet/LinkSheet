package app.linksheet.feature.engine.engine.rule.profileforward

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.engine.engine.*
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.rule.PreProcessorInput
import app.linksheet.feature.engine.engine.rule.PreProcessorRule
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ProfileForwardRuleTest : BaseUnitTest  {
    private val dispatcher = StandardTestDispatcher()

    private val rule = object : PreProcessorRule {
        private val workRelatedHost = "sso.mycompany.com"
        override suspend fun EngineRunContext.checkRule(input: PreProcessorInput): EngineResult? {
            if (input.url.host == workRelatedHost) {
                return ForwardOtherProfileResult(input.url)
            }

            return null
        }
    }

    private val base by lazy { ProfileForwardTestBase(dispatcher, rule) }

    @org.junit.Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.`test rule matched`()
    }
}
