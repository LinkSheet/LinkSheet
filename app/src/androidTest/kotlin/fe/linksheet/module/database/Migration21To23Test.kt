package fe.linksheet.module.database

import android.content.ContentValues
import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.CrossDatabaseMigrationCallback
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import app.linksheet.feature.libredirect.database.LibRedirectDatabase
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import fe.linksheet.module.database.migrations.Migration21to23
import fe.linksheet.testlib.instrument.InstrumentationTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class Migration21To23Test : InstrumentationTest {
    private val testDb = "migration-test"
    private val linkSheetTestDb = "linksheet-$testDb"
    private val libRedirectTestDb = "libredirect-$testDb"
    private val sqliteDriver = BundledSQLiteDriver()

    @get:Rule
    val linkSheetHelper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(linkSheetTestDb),
        driver = sqliteDriver,
        databaseClass = LinkSheetDatabase::class
    )

    @get:Rule
    val libRedirectHelper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = instrumentation,
        file = instrumentation.targetContext.getDatabasePath(libRedirectTestDb),
        driver = sqliteDriver,
        databaseClass = LibRedirectDatabase::class
    )

    @Test
    fun test() = runTest {
        val linkSheetDb = linkSheetHelper.createDatabase(21).apply {
            execSQL("""INSERT INTO lib_redirect_default (serviceKey, frontendKey, instanceUrl) VALUES ('bandcamp', 'tent', 'https://tent.bloat.cat')""")
            execSQL("""INSERT INTO lib_redirect_default (serviceKey, frontendKey, instanceUrl) VALUES ('youtube', 'invidious', 'RANDOM_INSTANCE')""")
            execSQL("""INSERT INTO lib_redirect_service_state (serviceKey, enabled) VALUES ('bandcamp', 0)""")
            execSQL("""INSERT INTO lib_redirect_service_state (serviceKey, enabled) VALUES ('youtube', 1)""")
            close()
        }


        val logger = Logger(Migration21To23Test::class.simpleName!!)
        val migrator = DefaultCrossDatabaseMigration()
        val migratedLinkSheetDb = linkSheetHelper.runMigrationsAndValidate(23, Migration21to23(logger, migrator).create())

        val tables = migrator.getTables()
        assertThat(tables).containsExactly("lib_redirect_default", "lib_redirect_service_state")
        assertThat(migrator.get("lib_redirect_default")).isNotNull().containsExactly(
            ContentValues().apply {
                put("serviceKey", "bandcamp")
                put("frontendKey", "tent")
                put("instanceUrl", "https://tent.bloat.cat")
            },
            ContentValues().apply {
                put("serviceKey", "youtube")
                put("frontendKey", "invidious")
                put("instanceUrl", "RANDOM_INSTANCE")
            }
        )
        assertThat(migrator.get("lib_redirect_service_state")).isNotNull().containsExactly(
            ContentValues().apply {
                put("serviceKey", "bandcamp")
                put("enabled", 0L)
            },
            ContentValues().apply {
                put("serviceKey", "youtube")
                put("enabled", 1L)
            }
        )


        val libRedirectDb = libRedirectHelper.createDatabase(1)

        val callback = CrossDatabaseMigrationCallback(migrator)
        callback.onOpen(libRedirectDb)

        val libRedirectDefaultCount = libRedirectDb.prepare("SELECT count(*) FROM lib_redirect_default").use { stmt ->
            stmt.step()
            stmt.getInt(0)
        }
        assertThat(libRedirectDefaultCount).isEqualTo(2)

        val libRedirectServiceStateCount = libRedirectDb.prepare("SELECT count(*) FROM lib_redirect_service_state").use { stmt ->
            stmt.step()
            stmt.getInt(0)
        }
        assertThat(libRedirectServiceStateCount).isEqualTo(2)

        libRedirectDb.close()
    }
}
