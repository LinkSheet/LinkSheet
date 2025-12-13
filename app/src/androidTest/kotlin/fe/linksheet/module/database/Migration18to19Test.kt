package fe.linksheet.module.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import fe.linksheet.testlib.instrument.InstrumentationTest
import mozilla.components.support.base.log.logger.Logger
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class Migration18to19Test : InstrumentationTest {

    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LinkSheetDatabase::class.java
    )


    @org.junit.Test
    fun test() {
        helper.createDatabase(testDb, 18).apply {
            execSQL("""INSERT INTO resolved_redirect VALUES ('https://t.co/test', 'https://linksheet.app')""")
            execSQL("""INSERT INTO resolved_redirect VALUES ('https://t.co/test', 'https://linksheet2.app')""")
            close()
        }

        val logger = Logger("Migration18to19Test")

        LinkSheetDatabase.create(targetContext, logger, testDb, DefaultCrossDatabaseMigration()).apply {
            openHelper.writableDatabase.close()
        }
    }
}
