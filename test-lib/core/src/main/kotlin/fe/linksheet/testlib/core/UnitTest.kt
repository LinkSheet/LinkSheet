package fe.linksheet.testlib.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

interface BaseUnitTest : KoinTest {
    @After
    fun teardown() {
        stopKoin()
    }
}

interface RobolectricTest : BaseUnitTest {
    val applicationContext: Context
        get() = ApplicationProvider.getApplicationContext()
}
