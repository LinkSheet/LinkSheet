package fe.linksheet.module.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.testlib.instrument.InstrumentationTest
import org.junit.Rule
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

    @org.junit.Test
    fun testMigrateFull() {
        runTest(2)
    }

    @org.junit.Test
    fun testMigrate12to17() {
        runTest(12)
    }

    @org.junit.Test
    fun testMigrate13to17() {
        runTest(13)
    }

    @org.junit.Test
    fun testMigrate14to17() {
        runTest(14)
    }

    @org.junit.Test
    fun testMigrate15to17() {
        runTest(15)
    }

    @org.junit.Test
    fun testMigrate16to17() {
        runTest(16)
    }
}
