package fe.linksheet.module.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fe.linksheet.module.database.dao.*
import fe.linksheet.module.database.dao.app.AppDomainVerificationStateDao
import fe.linksheet.module.database.dao.app.InstalledAppDao
import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.*
import fe.linksheet.module.database.entity.app.AppDomainVerificationState
import fe.linksheet.module.database.entity.app.InstalledApp
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import fe.linksheet.module.database.migrations.Migration1to2
import org.koin.dsl.module

val databaseModule = module {
    single<LinkSheetDatabase> {
        Room.databaseBuilder(get(), LinkSheetDatabase::class.java, "linksheet").addMigrations(Migration1to2).build()
    }
}

@Database(
    entities = [
        PreferredApp::class, AppSelectionHistory::class, WhitelistedNormalBrowser::class,
        WhitelistedInAppBrowser::class, ResolvedRedirect::class, LibRedirectDefault::class,
        LibRedirectServiceState::class, DisableInAppBrowserInSelected::class, Amp2HtmlMapping::class,
        InstalledApp::class, AppDomainVerificationState::class,
    ],
    version = 13,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
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
    abstract fun amp2HtmlMappingDao(): Amp2HtmlMappingDao
    abstract fun installedAppDao(): InstalledAppDao
    abstract fun appDomainVerificationStateDao(): AppDomainVerificationStateDao
}
