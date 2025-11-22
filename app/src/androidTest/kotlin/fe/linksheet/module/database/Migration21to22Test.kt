package fe.linksheet.module.database

import android.content.ContentValues
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.api.database.CrossDatabaseMigrationCallback
import app.linksheet.api.database.DefaultCrossDatabaseMigration
import app.linksheet.feature.libredirect.database.LibRedirectDatabase
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import fe.linksheet.module.database.migrations.Migration21to22
import fe.linksheet.testlib.instrument.InstrumentationTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class Migration21to22Test : InstrumentationTest {
    private val testDb = "migration-test"
    private val linkSheetTestDb = "linksheet-$testDb"
    private val libRedirectTestDb= "libredirect-$testDb"

    @get:Rule
    val linkSheetHelper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LinkSheetDatabase::class.java
    )

    @get:Rule
    val libRedirectHelper: MigrationTestHelper = MigrationTestHelper(
        instrumentation, LibRedirectDatabase::class.java
    )

    @Test
    fun test() {
        val linkSheetDb = linkSheetHelper.createDatabase(linkSheetTestDb, 21).apply {
            execSQL("""INSERT INTO lib_redirect_default (serviceKey, frontendKey, instanceUrl) VALUES ('bandcamp', 'tent', 'https://tent.bloat.cat')""")
            execSQL("""INSERT INTO lib_redirect_default (serviceKey, frontendKey, instanceUrl) VALUES ('youtube', 'invidious', 'RANDOM_INSTANCE')""")
            execSQL("""INSERT INTO lib_redirect_service_state (serviceKey, enabled) VALUES ('bandcamp', 0)""")
            execSQL("""INSERT INTO lib_redirect_service_state (serviceKey, enabled) VALUES ('youtube', 1)""")
            close()
        }

        val migrator = DefaultCrossDatabaseMigration()
        val migratedLinkSheetDb = linkSheetHelper.runMigrationsAndValidate(linkSheetTestDb, 22, true,Migration21to22(migrator))

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


        val libRedirectDb = libRedirectHelper.createDatabase(libRedirectTestDb, 1)

        val callback = CrossDatabaseMigrationCallback(migrator)
        callback.onOpen(libRedirectDb)

        val libRedirectDefaultCount = libRedirectDb.query("SELECT * FROM lib_redirect_default").use { it.count }
        assertThat(libRedirectDefaultCount).isEqualTo(2)

        val libRedirectServiceStateCount = libRedirectDb.query("SELECT * FROM lib_redirect_service_state").use { it.count }
        assertThat(libRedirectServiceStateCount).isEqualTo(2)

        libRedirectDb.close()
    }
}
