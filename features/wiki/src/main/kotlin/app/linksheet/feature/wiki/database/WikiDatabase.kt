package app.linksheet.feature.wiki.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.linksheet.feature.wiki.database.dao.WikiCacheDao
import app.linksheet.feature.wiki.database.entity.WikiCache

@Database(
    entities = [WikiCache::class],
    version = 1
)
abstract class WikiDatabase internal constructor() : RoomDatabase() {
    abstract fun wikiCacheDao(): WikiCacheDao

    companion object {
        internal fun create(context: Context, name: String): WikiDatabase {
            return Room.databaseBuilder(context, WikiDatabase::class.java, name).build()
        }
    }
}
