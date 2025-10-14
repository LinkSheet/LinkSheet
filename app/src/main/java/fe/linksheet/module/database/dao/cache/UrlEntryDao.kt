package fe.linksheet.module.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.cache.UrlEntry

@Dao
interface UrlEntryDao : BaseDao<UrlEntry> {
    @Query("SELECT * FROM url WHERE url = :url ORDER BY timestamp LIMIT 1")
    suspend fun getUrlEntry(url: String): UrlEntry?
}
