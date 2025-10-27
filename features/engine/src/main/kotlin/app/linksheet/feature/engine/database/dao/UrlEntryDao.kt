package app.linksheet.feature.engine.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.UrlEntry

@Dao
interface UrlEntryDao : BaseDao<UrlEntry> {
    @Query("SELECT * FROM url WHERE url = :url ORDER BY timestamp LIMIT 1")
    suspend fun getUrlEntry(url: String): UrlEntry?
}
