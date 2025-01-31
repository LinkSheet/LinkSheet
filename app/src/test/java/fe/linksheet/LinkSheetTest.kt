package fe.linksheet

import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

interface LinkSheetTest : KoinTest {

    @After
    fun teardown() {
        stopKoin()
    }
}
