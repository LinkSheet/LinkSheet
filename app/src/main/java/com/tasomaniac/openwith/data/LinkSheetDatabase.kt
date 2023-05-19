package com.tasomaniac.openwith.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import fe.linksheet.data.dao.AppSelectionHistoryDao
import fe.linksheet.data.dao.DisableInAppBrowserInSelectedDao
import fe.linksheet.data.dao.LibRedirectDefaultDao
import fe.linksheet.data.dao.LibRedirectServiceStateDao
import fe.linksheet.data.dao.ResolvedRedirectDao
import fe.linksheet.data.dao.WhitelistedBrowsersDao
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.entity.DisableInAppBrowserInSelected
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.LibRedirectServiceState
import fe.linksheet.data.entity.ResolvedRedirect
import fe.linksheet.data.entity.WhitelistedBrowser

@Database(
    entities = [
        PreferredApp::class, AppSelectionHistory::class, WhitelistedBrowser::class,
        ResolvedRedirect::class, LibRedirectDefault::class, LibRedirectServiceState::class,
        DisableInAppBrowserInSelected::class
    ],
    version = 8,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8)
    ],
    exportSchema = true
)
abstract class LinkSheetDatabase : RoomDatabase() {
    abstract fun preferredAppDao(): PreferredAppDao
    abstract fun appSelectionHistoryDao(): AppSelectionHistoryDao
    abstract fun whitelistedBrowsersDao(): WhitelistedBrowsersDao
    abstract fun disableInAppBrowserInSelectedDao(): DisableInAppBrowserInSelectedDao
    abstract fun resolvedRedirectDao(): ResolvedRedirectDao
    abstract fun libRedirectDefaultDao(): LibRedirectDefaultDao
    abstract fun libRedirectServiceStateDao(): LibRedirectServiceStateDao
}
