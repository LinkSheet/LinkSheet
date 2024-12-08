package fe.linksheet.module.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.BaseDao
import fe.linksheet.module.database.entity.cache.UrlEntry

@Dao
interface UrlEntryDao : BaseDao<UrlEntry> {
    @Query("SELECT * FROM url WHERE url = :url ORDER BY timestamp LIMIT 1")
    suspend fun getCacheEntry(url: String): UrlEntry?
}
