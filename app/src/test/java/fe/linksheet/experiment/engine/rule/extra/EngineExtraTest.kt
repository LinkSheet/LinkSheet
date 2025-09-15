package fe.linksheet.experiment.engine.rule.extra

import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineFlag
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.SourceAppExtra
import fe.linksheet.experiment.engine.context.findExtraOrNull
import fe.linksheet.experiment.engine.rule.PreProcessorInput
import fe.linksheet.experiment.engine.rule.PreProcessorRule
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


