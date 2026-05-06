package fe.linksheet.module.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.CrossDatabaseMigration
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.linksheet.testlib.instrument.InstrumentationTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MigrationTest : InstrumentationTest {
    private val testDb = "migration-test"
    private val sqliteDriver = BundledSQLiteDriver()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(testDb),
        driver = sqliteDriver,
        databaseClass = LinkSheetDatabase::class
    )

    private suspend fun test(version: Int, migration: CrossDatabaseMigration = DefaultCrossDatabaseMigration()) {
        helper.createDatabase(version).apply {
            close()
        }

        val logger = Logger(MigrationTest::class.simpleName!!)

        LinkSheetDatabase.create(targetContext, logger, testDb, migration).apply {
            close()
        }
    }

    @org.junit.Test
    fun testMigrateFull() = runTest {
         test(2)
    }

    @org.junit.Test
    fun migrate12ToLatest() = runTest {
        test(12)
    }

    @org.junit.Test
    fun migrate13ToLatest() = runTest {
        test(13)
    }

    @org.junit.Test
    fun migrate14ToLatest() = runTest {
        test(14)
    }

    @org.junit.Test
    fun migrate15ToLatest() = runTest {
        test(15)
    }

    @org.junit.Test
    fun testMigrate16toLatest() = runTest {
        test(16)
    }

    @org.junit.Test
    fun testMigrate21to22() = runTest {
        test(21)
    }
}
