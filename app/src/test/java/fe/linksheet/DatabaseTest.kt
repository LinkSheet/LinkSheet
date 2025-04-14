package fe.linksheet

import android.content.Context
import androidx.room.Room
import fe.linksheet.module.database.LinkSheetDatabase
import fe.linksheet.module.database.LinkSheetDatabase.Companion.configureAndBuild
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.internal.DebugLoggerDelegate
import fe.linksheet.testlib.core.RobolectricTest
import org.junit.After
import org.junit.Before

abstract class DatabaseTest : RobolectricTest {
    lateinit var database: LinkSheetDatabase

    fun createInMemoryTestDatabase(context: Context): LinkSheetDatabase {
        val logger = Logger(DebugLoggerDelegate(true, DatabaseTest::class))
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
    override fun stop() {
        println("[DatabaseTest] stop")
        super.stop()
        database.close()
    }
}
