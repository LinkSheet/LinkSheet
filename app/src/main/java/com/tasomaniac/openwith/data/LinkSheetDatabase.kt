package com.tasomaniac.openwith.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tasomaniac.openwith.data.migrations.Migration1to2
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.dao.AppSelectionHistoryDao
import fe.linksheet.data.dao.ResolvedRedirectDao
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.data.dao.WhitelistedBrowsersDao
import fe.linksheet.data.entity.ResolvedRedirect

@Database(
    entities = [PreferredApp::class, AppSelectionHistory::class, WhitelistedBrowser::class, ResolvedRedirect::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
    ],
    exportSchema = true
)
abstract class LinkSheetDatabase : RoomDatabase() {

    abstract fun preferredAppDao(): PreferredAppDao

    abstract fun appSelectionHistoryDao(): AppSelectionHistoryDao

    abstract fun whitelistedBrowsersDao(): WhitelistedBrowsersDao
    abstract fun resolvedRedirectDao(): ResolvedRedirectDao

    companion object {
        fun getDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LinkSheetDatabase::class.java,
            "linksheet"
        ).addMigrations(Migration1to2).build()
    }
}
