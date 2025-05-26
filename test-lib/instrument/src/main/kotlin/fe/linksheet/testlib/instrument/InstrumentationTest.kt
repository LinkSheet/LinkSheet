package fe.linksheet.testlib.instrument

import android.app.Instrumentation
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import fe.linksheet.testlib.core.BaseUnitTest

interface InstrumentationTest : BaseUnitTest {
    val instrumentation: Instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    val context: Context
        get() = instrumentation.context

    val targetContext: Context
        get() = instrumentation.targetContext

    val applicationContext: Context
        get() = targetContext.applicationContext
}
