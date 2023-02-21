package com.tasomaniac.openwith.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PreferredApp::class], version = 1, exportSchema = false)
abstract class LinkSheetDatabase : RoomDatabase() {

    abstract fun preferredAppDao(): PreferredAppDao

    companion object {
        fun getDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LinkSheetDatabase::class.java,
            "linksheet"
        ).build()
    }
}
