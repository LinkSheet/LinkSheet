package fe.linksheet.feature.wiki

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao

@Dao
interface WikiCacheDao : BaseDao<WikiCache> {
    @Query("SELECT * FROM wiki_cache WHERE url = :url AND timestamp")
    suspend fun getCachedText(url: String): WikiCache?
}
