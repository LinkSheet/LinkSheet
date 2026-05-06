package fe.linksheet.module.database

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.linksheet.testlib.instrument.InstrumentationTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class Migration18to19Test : InstrumentationTest {

    private val testDb = "migration-test"
    private val sqliteDriver = BundledSQLiteDriver()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(testDb),
        driver = sqliteDriver,
        databaseClass = LinkSheetDatabase::class
    )


    @org.junit.Test
    fun test() = runTest {
        helper.createDatabase(18).apply {
            execSQL("""INSERT INTO resolved_redirect VALUES ('https://t.co/test', 'https://linksheet.app')""")
            execSQL("""INSERT INTO resolved_redirect VALUES ('https://t.co/test', 'https://linksheet2.app')""")
            close()
        }

        val logger = Logger("Migration18to19Test")

        LinkSheetDatabase.create(targetContext, logger, testDb, DefaultCrossDatabaseMigration()).apply {
            close()
        }
    }
}
