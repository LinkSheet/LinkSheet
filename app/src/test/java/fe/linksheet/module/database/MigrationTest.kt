package fe.linksheet.module.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.UnitTest
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MigrationTest : UnitTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LinkSheetDatabase::class.java
    )

    private fun runTest(version: Int) {
        helper.createDatabase(testDb, version).apply {
           close()
        }

        val logger = Logger(DebugLoggerDelegate(MigrationTest::class))

        LinkSheetDatabase.create(targetContext, logger, testDb).apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    fun testMigrateFull() {
        runTest(2)
    }

    @Test
    fun testMigrate12to17() {
        runTest(12)
    }

    @Test
    fun testMigrate13to17() {
        runTest(13)
    }

    @Test
    fun testMigrate14to17() {
        runTest(14)
    }

    @Test
    fun testMigrate15to17() {
        runTest(15)
    }

    @Test
    fun testMigrate16to17() {
        runTest(16)
    }
}
