package app.linksheet.feature.browser.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.linksheet.feature.browser.database.dao.PrivateBrowsingBrowserDao
import app.linksheet.feature.browser.database.entity.PrivateBrowsingBrowser


@Database(
    entities = [PrivateBrowsingBrowser::class],
    version = 1
)
abstract class PrivateBrowsingDatabase internal constructor() : RoomDatabase() {
    abstract fun privateBrowsingBrowserDao(): PrivateBrowsingBrowserDao

    companion object {
        internal fun create(context: Context, name: String): PrivateBrowsingDatabase {
            return Room.databaseBuilder(context, PrivateBrowsingDatabase::class.java, name).build()
        }
    }
}
