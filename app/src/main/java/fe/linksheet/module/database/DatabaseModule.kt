package fe.linksheet.module.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.database.dao.*

import app.linksheet.feature.libredirect.database.dao.LibRedirectDefaultDao
import app.linksheet.feature.libredirect.database.dao.LibRedirectServiceStateDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.dao.resolver.ResolvedRedirectDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedInAppBrowsersDao
import fe.linksheet.module.database.dao.whitelisted.WhitelistedNormalBrowsersDao
import fe.linksheet.module.database.entity.*
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import fe.linksheet.module.database.migrations.Migration12to17
import fe.linksheet.module.database.migrations.Migration18to19
import fe.linksheet.module.database.migrations.Migration1to2
import fe.linksheet.module.database.migrations.Migration19to20
import fe.linksheet.module.database.migrations.Migration20to21
import fe.linksheet.module.log.Logger
import org.koin.dsl.module

val DatabaseModule = module {
    single<LinkSheetDatabase> {
        LinkSheetDatabase.create(context = get(), logger = createLogger<LinkSheetDatabase>(), name = "linksheet")
    }
}

@Database(
    entities = [
        PreferredApp::class, AppSelectionHistory::class, WhitelistedNormalBrowser::class,
        WhitelistedInAppBrowser::class, ResolvedRedirect::class, LibRedirectDefault::class,
        LibRedirectServiceState::class, DisableInAppBrowserInSelected::class, Amp2HtmlMapping::class,
//        ScenarioEntity::class
    ],
    version = 21,
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
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19),
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
//    abstract fun scenarioDao(): ScenarioDao

    companion object {
        private fun buildMigrations(logger: Logger): Array<Migration> {
            return arrayOf(
                Migration1to2,
                *Migration12to17(logger).create(),
                Migration18to19,
                Migration19to20,
                Migration20to21
            )
        }

        fun Builder<LinkSheetDatabase>.configureAndBuild(logger: Logger): LinkSheetDatabase {
            return addMigrations(*buildMigrations(logger)).build()
        }

        fun create(context: Context, logger: Logger, name: String): LinkSheetDatabase {
            return Room
                .databaseBuilder(context, LinkSheetDatabase::class.java, name)
                .configureAndBuild(logger)
        }
    }
}
