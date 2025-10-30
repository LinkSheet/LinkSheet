package fe.linksheet.feature.wiki.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.feature.wiki.database.entity.WikiCache

@Dao
interface WikiCacheDao : BaseDao<WikiCache> {
    @Query("SELECT * FROM wiki_cache WHERE url = :url AND timestamp")
    suspend fun getCachedText(url: String): WikiCache?
}
