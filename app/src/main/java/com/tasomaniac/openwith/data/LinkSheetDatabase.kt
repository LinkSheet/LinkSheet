package com.tasomaniac.openwith.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tasomaniac.openwith.data.migrations.Migration1to2
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.dao.AppSelectionHistoryDao
import fe.linksheet.data.dao.LibRedirectDefaultDao
import fe.linksheet.data.dao.ResolvedRedirectDao
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.data.dao.WhitelistedBrowsersDao
import fe.linksheet.data.entity.LibRedirectDefault
import fe.linksheet.data.entity.ResolvedRedirect

@Database(
    entities = [PreferredApp::class, AppSelectionHistory::class, WhitelistedBrowser::class, ResolvedRedirect::class, LibRedirectDefault::class],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6)
    ],
    exportSchema = true
)
abstract class LinkSheetDatabase : RoomDatabase() {

    abstract fun preferredAppDao(): PreferredAppDao

    abstract fun appSelectionHistoryDao(): AppSelectionHistoryDao

    abstract fun whitelistedBrowsersDao(): WhitelistedBrowsersDao
    abstract fun resolvedRedirectDao(): ResolvedRedirectDao

    abstract fun libRedirectDefaultDao(): LibRedirectDefaultDao

    companion object {
        fun getDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LinkSheetDatabase::class.java,
            "linksheet"
        ).addMigrations(Migration1to2).build()
    }
}
