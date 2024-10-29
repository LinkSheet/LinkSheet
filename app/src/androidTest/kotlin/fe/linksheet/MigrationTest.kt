package fe.linksheet

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fe.linksheet.module.database.LinkSheetDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        LinkSheetDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        helper.createDatabase(testDb, 2).apply {
            close()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        LinkSheetDatabase.create(context, testDb).apply {
            openHelper.writableDatabase.close()
        }
    }
}
