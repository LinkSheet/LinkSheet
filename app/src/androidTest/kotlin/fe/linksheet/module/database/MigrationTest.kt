package fe.linksheet.module.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.CrossDatabaseMigration
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import fe.linksheet.testlib.instrument.InstrumentationTest
import mozilla.components.support.base.log.logger.Logger
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MigrationTest : InstrumentationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LinkSheetDatabase::class.java
    )

    private fun runTest(version: Int, migration: CrossDatabaseMigration = DefaultCrossDatabaseMigration()) {
        helper.createDatabase(testDb, version).apply {
            close()
        }

        val logger = Logger(MigrationTest::class.simpleName!!)

        LinkSheetDatabase.create(targetContext, logger, testDb, migration).apply {
            openHelper.writableDatabase.close()
        }
    }

    @org.junit.Test
    fun testMigrateFull() {
        runTest(2)
    }

    @org.junit.Test
    fun migrate12ToLatest() {
        runTest(12)
    }

    @org.junit.Test
    fun migrate13ToLatest() {
        runTest(13)
    }

    @org.junit.Test
    fun migrate14ToLatest() {
        runTest(14)
    }

    @org.junit.Test
    fun migrate15ToLatest() {
        runTest(15)
    }

    @org.junit.Test
    fun testMigrate16toLatest() {
        runTest(16)
    }

    @org.junit.Test
    fun testMigrate21to22() {
        runTest(21)
    }
}
