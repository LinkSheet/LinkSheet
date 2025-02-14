package fe.linksheet

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RuntimeEnvironment

interface BaseTest : KoinTest {
    @After
    fun teardown() {
        stopKoin()
    }
}

interface UnitTest : BaseTest {
    val context: Context
        get() = ApplicationProvider.getApplicationContext<Context>()
}

interface RobolectricTest : BaseTest {
    val context: Context
        get() = RuntimeEnvironment.getApplication().applicationContext
}
