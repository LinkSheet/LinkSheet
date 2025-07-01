package fe.linksheet.module.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.testlib.instrument.InstrumentationTest
import org.junit.Rule
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MigrationTest : InstrumentationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LinkSheetDatabase::class.java
    )

    private fun runTest(version: Int) {
        helper.createDatabase(testDb, version).apply {
            close()
        }

        val logger = Logger(DebugLoggerDelegate(true, MigrationTest::class))

        LinkSheetDatabase.create(targetContext, logger, testDb).apply {
            openHelper.writableDatabase.close()
        }
    }

    @JunitTest
    fun testMigrateFull() {
        runTest(2)
    }

    @JunitTest
    fun testMigrate12to17() {
        runTest(12)
    }

    @JunitTest
    fun testMigrate13to17() {
        runTest(13)
    }

    @JunitTest
    fun testMigrate14to17() {
        runTest(14)
    }

    @JunitTest
    fun testMigrate15to17() {
        runTest(15)
    }

    @JunitTest
    fun testMigrate16to17() {
        runTest(16)
    }
}
