package fe.linksheet

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class InstalledAppMigrationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        LinkSheetDatabase::class.java
    )

    private fun runTest(version: Int) {
        helper.createDatabase(testDb, version).apply {
            close()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val logger = Logger(DebugLoggerDelegate(MigrationTest::class))

        LinkSheetDatabase.create(context, logger, testDb).apply {
            openHelper.writableDatabase.close()
        }
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
