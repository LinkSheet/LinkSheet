package fe.linksheet.module.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.database.dao.PreferredAppDao
import fe.linksheet.module.database.migrations.Migration1to2
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.module.database.dao.LibRedirectDefaultDao
import fe.linksheet.module.database.dao.LibRedirectServiceStateDao
import fe.linksheet.module.database.dao.ResolvedRedirectDao
import fe.linksheet.module.database.dao.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.dao.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.database.entity.LibRedirectServiceState
import fe.linksheet.module.database.entity.ResolvedRedirect
import fe.linksheet.module.database.entity.WhitelistedInAppBrowser
import fe.linksheet.module.database.entity.WhitelistedNormalBrowser
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), LinkSheetDatabase::class.java, "linksheet")
            .addMigrations(Migration1to2).build()
    }
}

@Database(
    entities = [
        PreferredApp::class, AppSelectionHistory::class, WhitelistedNormalBrowser::class,
        WhitelistedInAppBrowser::class, ResolvedRedirect::class, LibRedirectDefault::class,
        LibRedirectServiceState::class, DisableInAppBrowserInSelected::class
    ],
    version = 9,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9)
    ],
    exportSchema = true
)
abstract class LinkSheetDatabase : RoomDatabase() {
    abstract fun preferredAppDao(): PreferredAppDao
    abstract fun appSelectionHistoryDao(): AppSelectionHistoryDao
    abstract fun whitelistedBrowsersDao(): WhitelistedNormalBrowsersDao
    abstract fun whitelistedInAppBrowsersDao(): WhitelistedInAppBrowsersDao
    abstract fun disableInAppBrowserInSelectedDao(): DisableInAppBrowserInSelectedDao
    abstract fun resolvedRedirectDao(): ResolvedRedirectDao
    abstract fun libRedirectDefaultDao(): LibRedirectDefaultDao
    abstract fun libRedirectServiceStateDao(): LibRedirectServiceStateDao
}
