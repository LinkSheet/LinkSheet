package fe.linksheet

import android.content.Context
import androidx.room.Room
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.LinkSheetDatabase.Companion.configureAndBuild
import mozilla.components.support.base.log.logger.Logger
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//abstract class DatabaseTest(val closeDb: Boolean = true) : BaseUnitTest  {
//    val database: LinkSheetDatabase by lazy {
//        createInMemoryTestDatabase(applicationContext)
//    }
//
//    fun createInMemoryTestDatabase(context: Context): LinkSheetDatabase {
//        val logger = Logger(DebugLoggerDelegate(true, DatabaseTest::class))
//        val database = Room
//            .inMemoryDatabaseBuilder(context, LinkSheetDatabase::class.java)
//            .configureAndBuild(logger)
//
//        return database
//    }
//
//    @After
//    override fun stop() {
//        println("[DatabaseTest] stop")
//        if (closeDb) {
//            database.close()
//        }
//        super.stop()
//    }
//}

class DatabaseTestRule(private val applicationContext: Context) : TestWatcher() {

    val database: LinkSheetDatabase = createInMemoryTestDatabase(applicationContext)

    fun createInMemoryTestDatabase(context: Context): LinkSheetDatabase {
        val database = Room
            .inMemoryDatabaseBuilder(context, LinkSheetDatabase::class.java)
            .configureAndBuild(Logger("DatabaseTestRule"), DefaultCrossDatabaseMigration())

        return database
    }

    override fun starting(description: Description?) {
    }

    override fun finished(description: Description?) {
        database.close()
    }
}
