package fe.linksheet

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RuntimeEnvironment

interface BaseUnitTest : KoinTest {
    @After
    fun teardown() {
        stopKoin()
    }
}

interface UnitTest : BaseUnitTest {
    val context: Context
        get() = ApplicationProvider.getApplicationContext<Context>()
}

interface RobolectricTest : BaseUnitTest {
    val context: Context
        get() = RuntimeEnvironment.getApplication().applicationContext
}
