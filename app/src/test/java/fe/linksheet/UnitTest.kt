package fe.linksheet

import android.app.Instrumentation
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

interface BaseUnitTest : KoinTest {
    @After
    fun stop() {
        println("[BaseUnitTest] stop")
        stopKoin()
    }
}

interface UnitTest : BaseUnitTest {
    val instrumentation: Instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    val targetContext: Context
        get() = instrumentation.targetContext

    val applicationContext: Context
        get() = targetContext.applicationContext

    @After
    override fun stop() {
        println("[UnitTest] stop")
        // Doesn't propagate otherwise
        super.stop()
    }
}
