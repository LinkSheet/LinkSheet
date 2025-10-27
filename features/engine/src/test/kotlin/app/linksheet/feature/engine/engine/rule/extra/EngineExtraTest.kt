package app.linksheet.feature.engine.engine.rule.extra

import app.linksheet.feature.engine.engine.EngineResult
import app.linksheet.feature.engine.engine.context.EngineFlag
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.context.SourceAppExtra
import app.linksheet.feature.engine.engine.context.findExtraOrNull
import app.linksheet.feature.engine.engine.rule.PreProcessorInput
import app.linksheet.feature.engine.engine.rule.PreProcessorRule
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.util.AndroidAppPackage
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class EngineExtraTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    private val rule = object : PreProcessorRule {
        private val chromePackage = AndroidAppPackage("com.google.chrome")

        override suspend fun EngineRunContext.checkRule(input: PreProcessorInput): EngineResult? {
            val extra = findExtraOrNull<SourceAppExtra>()
            if (extra?.appPackage == chromePackage.packageName) {
                flags.add(EngineFlag.DisablePreview)
            }

            return empty()
        }
    }

    private val base by lazy { EngineExtraTestBase(dispatcher, rule) }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.test()
    }
}


