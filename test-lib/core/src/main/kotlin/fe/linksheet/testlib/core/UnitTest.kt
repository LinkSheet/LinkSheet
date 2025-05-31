package fe.linksheet.testlib.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.jupiter.api.AfterEach
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatformTools
import org.koin.test.KoinTest

interface BaseUnitTest : KoinTest {
    @After
    @AfterEach
    fun stop() {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) {
            stopKoin()
        }
    }
}

interface RobolectricTest : BaseUnitTest {
    val applicationContext: Context
        get() = ApplicationProvider.getApplicationContext()
}

typealias JunitTest = org.junit.Test
