package app.linksheet.feature.engine.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.CachedHtml

@Dao
interface HtmlCacheDao : BaseDao<CachedHtml> {
    @Query("SELECT * FROM html_cache WHERE id = :urlId")
    suspend fun getCachedHtml(urlId: Long): CachedHtml?
}
