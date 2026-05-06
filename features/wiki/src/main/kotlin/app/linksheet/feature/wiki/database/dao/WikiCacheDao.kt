package app.linksheet.feature.wiki.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.wiki.database.entity.WikiCache

@Dao
interface WikiCacheDao : BaseDao<WikiCache> {
    @Query("SELECT * FROM wiki_cache WHERE url = :url AND timestamp")
    suspend fun getCachedText(url: String): WikiCache?
}
