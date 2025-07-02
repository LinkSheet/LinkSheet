package fe.linksheet

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.LinkSheetDatabase.Companion.configureAndBuild
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.After
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
        val logger = Logger(DebugLoggerDelegate(true, DatabaseTestRule::class))
        val database = Room
            .inMemoryDatabaseBuilder(context, LinkSheetDatabase::class.java)
            .configureAndBuild(logger)

        return database
    }

    override fun starting(description: Description?) {
    }

    override fun finished(description: Description?) {
        database.close()
    }
}
