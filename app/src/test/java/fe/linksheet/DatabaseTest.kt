package fe.linksheet

import android.content.Context
import androidx.room.Room
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.LinkSheetDatabase.Companion.configureAndBuild
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import org.junit.After
import org.junit.Before

abstract class DatabaseTest : UnitTest {
    lateinit var database: LinkSheetDatabase

    fun createInMemoryTestDatabase(context: Context): LinkSheetDatabase {
        val logger = Logger(DebugLoggerDelegate(DatabaseTest::class))
        val database = Room
            .inMemoryDatabaseBuilder(context, LinkSheetDatabase::class.java)
            .configureAndBuild(logger)

        return database
    }

    @Before
    fun setup() {
        database = createInMemoryTestDatabase(applicationContext)
    }

    @After
    fun close() {
        database.close()
    }
}
