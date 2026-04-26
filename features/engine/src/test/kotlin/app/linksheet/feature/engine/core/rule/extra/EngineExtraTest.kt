package app.linksheet.feature.engine.core.rule.extra

import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.context.SourceAppExtra
import app.linksheet.feature.engine.core.context.findExtraOrNull
import app.linksheet.feature.engine.core.rule.PreProcessorInput
import app.linksheet.feature.engine.core.rule.PreProcessorRule
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

        context(context: EngineRunContext)
        override suspend fun checkRule(input: PreProcessorInput): EngineResult? {
            val extra = context.findExtraOrNull<SourceAppExtra>()
            if (extra?.appPackage == chromePackage.packageName) {
                context.flags.add(EngineFlag.DisablePreview)
            }

            return context.empty()
        }
    }

    private val base by lazy { EngineExtraTestBase(dispatcher, rule) }

    @Test
    fun `test rule matched`() = runTest(dispatcher) {
        base.test()
    }
}


